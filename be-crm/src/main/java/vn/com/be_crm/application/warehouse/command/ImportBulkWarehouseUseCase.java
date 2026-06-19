package vn.com.be_crm.application.warehouse.command;

import vn.com.be_crm.application.shared.dto.ImportBulkResult;
import vn.com.be_crm.application.shared.dto.ImportRowError;
import vn.com.be_crm.application.warehouse.dto.ImportBulkWarehouseCommand;
import vn.com.be_crm.application.warehouse.dto.ImportWarehouseRowCommand;
import vn.com.be_crm.domain.warehouse.entity.Warehouse;
import vn.com.be_crm.domain.warehouse.repository.IWarehouseRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        // Duyệt từng dòng; rowNum = i + 2 vì dòng 1 là header trong file Excel
        for (int i = 0; i < cmd.rows().size(); i++) {
            int rowNum = i + 2;
            ImportWarehouseRowCommand row = cmd.rows().get(i);
            try {
                if (row.name() == null || row.name().isBlank()) {
                    // Gom lỗi theo từng dòng, không hủy cả lô — các dòng hợp lệ vẫn được lưu
                    errors.add(new ImportRowError(rowNum, "Trường 'Tên kho' là bắt buộc"));
                    continue;
                }
                // Xác định nhánh thao tác theo importType: CREATE / UPDATE / BOTH
                boolean isUpdate = "UPDATE".equals(cmd.importType()) || "BOTH".equals(cmd.importType());
                boolean isCreate = "CREATE".equals(cmd.importType()) || "BOTH".equals(cmd.importType());

                // Tìm bản ghi trùng theo khóa duy nhất để cập nhật (chỉ khi cho phép UPDATE)
                Optional<Warehouse> existing = Optional.empty();
                if (isUpdate && row.code() != null && !row.code().isBlank())
                    existing = repo.findByCode(row.code());

                // Có bản ghi → cập nhật (giữ field cũ, ghi đè field có trong file)
                if (existing.isPresent()) {
                    Warehouse e = existing.get();
                    repo.save(Warehouse.builder()
                            .id(e.getId()).code(e.getCode()).name(row.name())
                            .address(row.address() != null ? row.address() : e.getAddress())
                            .isActive(e.getIsActive())
                            .createdAt(e.getCreatedAt()).build());
                    success++;
                // Chưa có và được phép tạo mới → thêm mới
                } else if (isCreate) {
                    String code = row.code() != null && !row.code().isBlank()
                            ? row.code()
                            : "KHO-" + System.currentTimeMillis() + "-" + rowNum;
                    repo.save(Warehouse.builder()
                            .code(code).name(row.name())
                            .address(row.address())
                            .isActive(true)
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
}
