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
        for (int i = 0; i < cmd.rows().size(); i++) {
            int rowNum = i + 2;
            ImportProductRowCommand row = cmd.rows().get(i);
            try {
                if (row.name() == null || row.name().isBlank()) {
                    errors.add(new ImportRowError(rowNum, "Trường 'Tên sản phẩm' là bắt buộc"));
                    continue;
                }
                String sku = row.sku() != null && !row.sku().isBlank()
                        ? row.sku()
                        : "SKU-" + System.currentTimeMillis() + "-" + rowNum;
                ProductType type = parseType(row.type());
                repo.save(Product.builder()
                        .sku(sku).name(row.name())
                        .type(type != null ? type : ProductType.goods)
                        .unit(row.unit()).basePrice(row.basePrice())
                        .costPrice(row.costPrice()).vatRate(row.vatRate())
                        .barcode(row.barcode()).description(row.description())
                        .isActive(true).isDiscontinued(false)
                        .build());
                success++;
            } catch (Exception ex) {
                errors.add(new ImportRowError(rowNum, ex.getMessage() != null ? ex.getMessage() : "Lỗi không xác định"));
            }
        }
        return new ImportBulkResult(success, errors.size(), errors);
    }

    private ProductType parseType(String s) {
        if (s == null || s.isBlank()) return null;
        try { return ProductType.valueOf(s.trim().toLowerCase()); }
        catch (Exception e) { return null; }
    }
}
