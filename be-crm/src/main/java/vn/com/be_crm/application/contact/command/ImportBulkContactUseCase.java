package vn.com.be_crm.application.contact.command;

import vn.com.be_crm.application.contact.dto.ImportBulkContactCommand;
import vn.com.be_crm.application.contact.dto.ImportContactRowCommand;
import vn.com.be_crm.application.shared.dto.ImportBulkResult;
import vn.com.be_crm.application.shared.dto.ImportRowError;
import vn.com.be_crm.domain.contact.entity.Contact;
import vn.com.be_crm.domain.contact.repository.IContactRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Use case nhập hàng loạt Contact từ file Excel/CSV. */
public class ImportBulkContactUseCase {
    private final IContactRepository repo;

    /** @param repo port lưu trữ */
    public ImportBulkContactUseCase(IContactRepository repo) { this.repo = repo; }

    /**
     * Xử lý nhập hàng loạt Contact.
     * @param cmd dữ liệu import @return kết quả nhập
     */
    public ImportBulkResult execute(ImportBulkContactCommand cmd) {
        List<ImportRowError> errors = new ArrayList<>();
        int success = 0;
        // Duyệt từng dòng; rowNum = i + 2 vì dòng 1 là header trong file Excel
        for (int i = 0; i < cmd.rows().size(); i++) {
            int rowNum = i + 2;
            ImportContactRowCommand row = cmd.rows().get(i);
            try {
                if (row.fullName() == null || row.fullName().isBlank()) {
                    // Gom lỗi theo từng dòng, không hủy cả lô — các dòng hợp lệ vẫn được lưu
                    errors.add(new ImportRowError(rowNum, "Trường 'Họ và tên' là bắt buộc"));
                    continue;
                }
                // Xác định nhánh thao tác theo importType: CREATE / UPDATE / BOTH
                boolean isUpdate = "UPDATE".equals(cmd.importType()) || "BOTH".equals(cmd.importType());
                boolean isCreate = "CREATE".equals(cmd.importType()) || "BOTH".equals(cmd.importType());

                // Tìm bản ghi trùng theo khóa duy nhất để cập nhật (chỉ khi cho phép UPDATE)
                Optional<Contact> existing = Optional.empty();
                if (isUpdate && row.email() != null && !row.email().isBlank())
                    existing = repo.findByEmail(row.email());

                // Có bản ghi → cập nhật (giữ field cũ, ghi đè field có trong file)
                if (existing.isPresent()) {
                    Contact e = existing.get();
                    repo.save(Contact.builder()
                            .id(e.getId())
                            .customerId(e.getCustomerId()).assignedUserId(e.getAssignedUserId())
                            .salutation(e.getSalutation())
                            .fullName(row.fullName())
                            .title(e.getTitle()).department(e.getDepartment())
                            .position(row.position() != null ? row.position() : e.getPosition())
                            .email(row.email() != null ? row.email() : e.getEmail())
                            .workEmail(e.getWorkEmail()).personalEmail(e.getPersonalEmail())
                            .zalo(e.getZalo()).source(e.getSource())
                            .gender(e.getGender()).dateOfBirth(e.getDateOfBirth())
                            .address(row.address() != null ? row.address() : e.getAddress())
                            .doNotCall(e.isDoNotCall()).doNotEmail(e.isDoNotEmail())
                            .isPrimary(e.getIsPrimary())
                            .createdAt(e.getCreatedAt()).build());
                    success++;
                // Chưa có và được phép tạo mới → thêm mới
                } else if (isCreate) {
                    repo.save(Contact.builder()
                            .fullName(row.fullName())
                            .position(row.position())
                            .email(row.email())
                            .address(row.address())
                            .isPrimary(false)
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
