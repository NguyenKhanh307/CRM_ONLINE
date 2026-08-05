package vn.com.be_crm.application.contact.command;

import vn.com.be_crm.application.contact.dto.ImportBulkContactCommand;
import vn.com.be_crm.application.contact.dto.ImportContactRowCommand;
import vn.com.be_crm.core.dto.import_.ImportBulkResult;
import vn.com.be_crm.core.dto.import_.ImportRowError;
import vn.com.be_crm.domain.contact.entity.Contact;
import vn.com.be_crm.domain.contact.enums.ContactGender;
import vn.com.be_crm.domain.contact.repository.IContactRepository;

import java.time.LocalDate;

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

                Long ownerId = resolveOwnerId(cmd);
                ContactGender gender = parseGender(row.gender());
                LocalDate dateOfBirth = parseDate(row.dateOfBirth());

                // Có bản ghi → cập nhật (giữ field cũ, ghi đè field có trong file)
                if (existing.isPresent()) {
                    Contact e = existing.get();
                    repo.save(Contact.builder()
                            .id(e.getId())
                            .customerId(row.customerId() != null ? row.customerId() : e.getCustomerId())
                            .assignedUserId(ownerId != null ? ownerId : e.getAssignedUserId())
                            .salutation(row.salutation() != null ? row.salutation() : e.getSalutation())
                            .fullName(row.fullName())
                            .title(row.title() != null ? row.title() : e.getTitle())
                            .department(row.department() != null ? row.department() : e.getDepartment())
                            .email(row.email() != null ? row.email() : e.getEmail())
                            .zalo(row.zalo() != null ? row.zalo() : e.getZalo())
                            .source(row.source() != null ? row.source() : e.getSource())
                            .gender(gender != null ? gender : e.getGender())
                            .dateOfBirth(dateOfBirth != null ? dateOfBirth : e.getDateOfBirth())
                            .isPrimary(row.isPrimary() != null ? row.isPrimary() : e.getIsPrimary())
                            .createdAt(e.getCreatedAt()).build());
                    success++;
                // Chưa có và được phép tạo mới → thêm mới
                } else if (isCreate) {
                    repo.save(Contact.builder()
                            .customerId(row.customerId()).assignedUserId(ownerId)
                            .salutation(row.salutation())
                            .fullName(row.fullName())
                            .title(row.title()).department(row.department())
                            .email(row.email())
                            .zalo(row.zalo()).source(row.source())
                            .gender(gender).dateOfBirth(dateOfBirth)
                            .isPrimary(row.isPrimary() != null && row.isPrimary())
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

    private Long resolveOwnerId(ImportBulkContactCommand cmd) {
        if ("SPECIFIC".equals(cmd.ownerMode())) return cmd.specificOwnerId();
        return null;
    }

    private ContactGender parseGender(String s) {
        if (s == null || s.isBlank()) return null;
        try { return ContactGender.valueOf(s.trim().toLowerCase()); }
        catch (Exception e) { return null; }
    }

    private LocalDate parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        try { return LocalDate.parse(s.trim()); }
        catch (Exception e) { return null; }
    }
}
