package vn.com.be_crm.application.product.command;

import vn.com.be_crm.application.product.dto.ImportBulkProductCommand;
import vn.com.be_crm.application.product.dto.ImportProductRowCommand;
import vn.com.be_crm.application.shared.dto.ImportBulkResult;
import vn.com.be_crm.application.shared.dto.ImportRowError;
import vn.com.be_crm.domain.product.entity.Product;
import vn.com.be_crm.domain.product.enums.ProductType;
import vn.com.be_crm.domain.product.repository.IProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Use case nhập hàng loạt Product từ file Excel/CSV. */
public class ImportBulkProductUseCase {
    private final IProductRepository repo;

    /** @param repo port lưu trữ */
    public ImportBulkProductUseCase(IProductRepository repo) { this.repo = repo; }

    /**
     * Xử lý nhập hàng loạt Product.
     * @param cmd dữ liệu import @return kết quả nhập
     */
    public ImportBulkResult execute(ImportBulkProductCommand cmd) {
        List<ImportRowError> errors = new ArrayList<>();
        int success = 0;
        // Duyệt từng dòng; rowNum = i + 2 vì dòng 1 là header trong file Excel
        for (int i = 0; i < cmd.rows().size(); i++) {
            int rowNum = i + 2;
            ImportProductRowCommand row = cmd.rows().get(i);
            try {
                if (row.name() == null || row.name().isBlank()) {
                    // Gom lỗi theo từng dòng, không hủy cả lô — các dòng hợp lệ vẫn được lưu
                    errors.add(new ImportRowError(rowNum, "Trường 'Tên sản phẩm' là bắt buộc"));
                    continue;
                }
                ProductType type = parseType(row.type());

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
                            .categoryId(e.getCategoryId())
                            .type(type != null ? type : e.getType())
                            .unit(row.unit() != null ? row.unit() : e.getUnit())
                            .basePrice(row.basePrice() != null ? row.basePrice() : e.getBasePrice())
                            .costPrice(row.costPrice() != null ? row.costPrice() : e.getCostPrice())
                            .vatRate(row.vatRate() != null ? row.vatRate() : e.getVatRate())
                            .description(row.description() != null ? row.description() : e.getDescription())
                            .isActive(e.getIsActive()).isDiscontinued(e.getIsDiscontinued())
                            .createdAt(e.getCreatedAt()).build());
                    success++;
                // Chưa có và được phép tạo mới → thêm mới
                } else if (isCreate) {
                    String sku = row.sku() != null && !row.sku().isBlank()
                            ? row.sku()
                            : "SKU-" + System.currentTimeMillis() + "-" + rowNum;
                    repo.save(Product.builder()
                            .sku(sku).name(row.name())
                            .type(type != null ? type : ProductType.goods)
                            .unit(row.unit()).basePrice(row.basePrice())
                            .costPrice(row.costPrice()).vatRate(row.vatRate())
                            .description(row.description())
                            .isActive(true).isDiscontinued(false)
                            .build());
                    success++;
                }
            } catch (Exception ex) {
                // Gom lỗi theo từng dòng, không hủy cả lô — các dòng hợp lệ vẫn được lưu
                errors.add(new ImportRowError(rowNum, ex.getMessage() != null ? ex.getMessage() : "Lỗi không xác định"));
            }
        }
        return new ImportBulkResult(success, errors.size(), errors);
    }

    private ProductType parseType(String s) {
        if (s == null || s.isBlank()) return null;
        String v = s.trim();
        // Chấp nhận nhãn tiếng Việt ('Dịch vụ') lẫn tên enum ('service')
        if (v.equalsIgnoreCase("Dịch vụ") || v.equalsIgnoreCase("service")) return ProductType.service;
        if (v.equalsIgnoreCase("Vật tư hàng hóa") || v.equalsIgnoreCase("goods")) return ProductType.goods;
        return null;
    }
}
