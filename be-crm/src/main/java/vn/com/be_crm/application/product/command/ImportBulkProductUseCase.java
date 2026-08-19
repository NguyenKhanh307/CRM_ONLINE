package vn.com.be_crm.application.product.command;

import vn.com.be_crm.application.product.dto.ImportBulkProductCommand;
import vn.com.be_crm.application.product.dto.ImportProductRowCommand;
import vn.com.be_crm.core.dto.import_.ImportBulkResult;
import vn.com.be_crm.core.dto.import_.ImportRowError;
import vn.com.be_crm.core.tx.port.ITransactionRunner;
import vn.com.be_crm.domain.product.entity.Product;
import vn.com.be_crm.domain.product.enums.ProductStatus;
import vn.com.be_crm.domain.product.enums.ProductType;
import vn.com.be_crm.domain.product.repository.IProductRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

// nhập hàng loạt Product từ file Excel/CSV — validate TOÀN BỘ trước (pass 1, không đụng DB), chỉ
// khi sạch hết mới lưu TOÀN BỘ trong 1 transaction (pass 2)
public class ImportBulkProductUseCase {
    private final IProductRepository repo;
    private final ITransactionRunner txRunner;

    /** @param repo port lưu trữ @param txRunner chạy pass 2 trong 1 transaction */
    public ImportBulkProductUseCase(IProductRepository repo, ITransactionRunner txRunner) {
        this.repo = repo;
        this.txRunner = txRunner;
    }

    /**
     * Xử lý nhập hàng loạt Product.
     * @param cmd dữ liệu import @return kết quả nhập
     */
    public ImportBulkResult execute(ImportBulkProductCommand cmd) {
        List<ImportRowError> errors = new ArrayList<>();
        Set<String> seenSkus = new HashSet<>();

        // ----- Pass 1: validate mọi dòng, không chạm DB (trừ check trùng SKU đọc-thôi bên dưới) -----
        for (int i = 0; i < cmd.rows().size(); i++) {
            int rowNum = i + 2;
            ImportProductRowCommand row = cmd.rows().get(i);
            if (row.name() == null || row.name().isBlank()) {
                errors.add(new ImportRowError(rowNum, "Trường 'Tên sản phẩm' là bắt buộc"));
                continue;
            }
            String err = null;
            if (row.type() != null && !row.type().isBlank() && parseType(row.type()) == null) {
                err = "Trường \"Loại\" giá trị không hợp lệ: \"" + row.type() + "\"";
            }
            if (err == null && row.status() != null && !row.status().isBlank() && parseStatus(row.status()) == null) {
                err = "Trường \"Trạng thái\" giá trị không hợp lệ: \"" + row.status() + "\"";
            }
            if (err != null) errors.add(new ImportRowError(rowNum, err));

            // check trùng SKU khi tạo mới thuần — tránh crash cả file do vi phạm unique constraint ở Pass 2
            if ("CREATE".equals(cmd.importType()) && row.sku() != null && !row.sku().isBlank()) {
                String sku = row.sku().trim();
                if (!seenSkus.add(sku)) {
                    errors.add(new ImportRowError(rowNum, "Mã SKU \"" + sku + "\" bị trùng với dòng khác trong file"));
                } else if (repo.findBySku(sku).isPresent()) {
                    errors.add(new ImportRowError(rowNum, "Mã SKU \"" + sku + "\" đã tồn tại, vui lòng dùng mã khác hoặc chọn chế độ Cập nhật"));
                }
            }
        }
        if (!errors.isEmpty()) return new ImportBulkResult(0, errors.size(), errors);

        // ----- Pass 2: mọi dòng đã sạch — lưu toàn bộ trong 1 transaction -----
        int[] success = {0};
        try {
            txRunner.run(() -> {
                for (int i = 0; i < cmd.rows().size(); i++) {
                    int rowNum = i + 2;
                    ImportProductRowCommand row = cmd.rows().get(i);
                    ProductType type = parseType(row.type());
                    ProductStatus status = parseStatus(row.status());

                    // Xác định nhánh thao tác theo importType: CREATE / UPDATE / BOTH
                    boolean isUpdate = "UPDATE".equals(cmd.importType()) || "BOTH".equals(cmd.importType());
                    boolean isCreate = "CREATE".equals(cmd.importType()) || "BOTH".equals(cmd.importType());

                    // Tìm bản ghi trùng theo khóa duy nhất để cập nhật (chỉ khi cho phép UPDATE)
                    Optional<Product> existing = Optional.empty();
                    if (isUpdate && row.sku() != null && !row.sku().isBlank())
                        existing = repo.findBySku(row.sku());

                    // Có bản ghi → cập nhật (giữ field cũ, ghi đè field có trong file)
                    if (existing.isPresent()) {
                        Product e = existing.get();
                        repo.save(Product.builder()
                                .id(e.getId()).sku(e.getSku()).name(row.name())
                                .categoryId(row.categoryId() != null ? row.categoryId() : e.getCategoryId())
                                .type(type != null ? type : e.getType())
                                .unit(row.unit() != null ? row.unit() : e.getUnit())
                                .basePrice(row.basePrice() != null ? row.basePrice() : e.getBasePrice())
                                .costPrice(row.costPrice() != null ? row.costPrice() : e.getCostPrice())
                                .vatRate(row.vatRate() != null ? row.vatRate() : e.getVatRate())
                                .description(row.description() != null ? row.description() : e.getDescription())
                                .status(status != null ? status : e.getStatus())
                                .createdAt(e.getCreatedAt()).build());
                        success[0]++;
                    // Chưa có và được phép tạo mới → thêm mới
                    } else if (isCreate) {
                        String sku = row.sku() != null && !row.sku().isBlank()
                                ? row.sku()
                                : "SKU-" + System.currentTimeMillis() + "-" + rowNum;
                        repo.save(Product.builder()
                                .sku(sku).name(row.name())
                                .categoryId(row.categoryId())
                                .type(type != null ? type : ProductType.goods)
                                .unit(row.unit()).basePrice(row.basePrice())
                                .costPrice(row.costPrice()).vatRate(row.vatRate())
                                .description(row.description())
                                .status(status != null ? status : ProductStatus.active)
                                .build());
                        success[0]++;
                    }
                }
            });
        } catch (Exception ex) {
            return new ImportBulkResult(0, 1, List.of(new ImportRowError(0,
                    ex.getMessage() != null ? ex.getMessage() : "Lỗi không xác định khi lưu dữ liệu")));
        }
        return new ImportBulkResult(success[0], 0, List.of());
    }

    private ProductType parseType(String s) {
        if (s == null || s.isBlank()) return null;
        String v = s.trim();
        // Chấp nhận nhãn tiếng Việt ('Dịch vụ') lẫn tên enum ('service')
        if (v.equalsIgnoreCase("Dịch vụ") || v.equalsIgnoreCase("service")) return ProductType.service;
        if (v.equalsIgnoreCase("Vật tư hàng hóa") || v.equalsIgnoreCase("goods")) return ProductType.goods;
        return null;
    }

    private ProductStatus parseStatus(String s) {
        if (s == null || s.isBlank()) return null;
        try { return ProductStatus.valueOf(s.trim().toLowerCase()); }
        catch (Exception e) { return null; }
    }
}
