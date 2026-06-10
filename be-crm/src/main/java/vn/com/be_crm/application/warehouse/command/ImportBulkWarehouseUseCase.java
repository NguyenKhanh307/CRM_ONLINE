package vn.com.be_crm.application.warehouse.command;

import vn.com.be_crm.application.shared.dto.ImportBulkResult;
import vn.com.be_crm.application.shared.dto.ImportRowError;
import vn.com.be_crm.application.warehouse.dto.ImportBulkWarehouseCommand;
import vn.com.be_crm.application.warehouse.dto.ImportWarehouseRowCommand;
import vn.com.be_crm.domain.warehouse.entity.Warehouse;
import vn.com.be_crm.domain.warehouse.repository.IWarehouseRepository;

import java.util.ArrayList;
import java.util.List;

/** Use case nhập hàng loạt Warehouse từ file Excel/CSV. */
public class ImportBulkWarehouseUseCase {
    private final IWarehouseRepository repo;

    /** @param repo port lưu trữ */
    public ImportBulkWarehouseUseCase(IWarehouseRepository repo) { this.repo = repo; }

    /**
     * Xử lý nhập hàng loạt Warehouse.
     * @param cmd dữ liệu import @return kết quả nhập
     */
    public ImportBulkResult execute(ImportBulkWarehouseCommand cmd) {
        List<ImportRowError> errors = new ArrayList<>();
        int success = 0;
        for (int i = 0; i < cmd.rows().size(); i++) {
            int rowNum = i + 2;
            ImportWarehouseRowCommand row = cmd.rows().get(i);
            try {
                if (row.name() == null || row.name().isBlank()) {
                    errors.add(new ImportRowError(rowNum, "Trường 'Tên kho' là bắt buộc"));
                    continue;
                }
                String code = row.code() != null && !row.code().isBlank()
                        ? row.code()
                        : "KHO-" + System.currentTimeMillis() + "-" + rowNum;
                repo.save(Warehouse.builder()
                        .code(code).name(row.name())
                        .address(row.address())
                        .isActive(true)
                        .build());
                success++;
            } catch (Exception ex) {
                errors.add(new ImportRowError(rowNum, ex.getMessage() != null ? ex.getMessage() : "Lỗi không xác định"));
            }
        }
        return new ImportBulkResult(success, errors.size(), errors);
    }
}
