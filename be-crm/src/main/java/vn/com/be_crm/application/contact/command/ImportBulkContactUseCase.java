package vn.com.be_crm.application.contact.command;

import vn.com.be_crm.application.contact.dto.ImportBulkContactCommand;
import vn.com.be_crm.application.contact.dto.ImportContactRowCommand;
import vn.com.be_crm.core.dto.import_.ImportBulkResult;
import vn.com.be_crm.core.dto.import_.ImportRowError;
import vn.com.be_crm.core.tx.port.ITransactionRunner;
import vn.com.be_crm.core.util.ImportValidators;
import vn.com.be_crm.domain.contact.entity.Contact;
import vn.com.be_crm.domain.contact.enums.ContactGender;
import vn.com.be_crm.domain.contact.repository.IContactRepository;

import java.time.LocalDate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// nhập hàng loạt Contact từ file Excel/CSV — validate TOÀN BỘ trước (pass 1, không đụng DB), chỉ khi
// sạch hết mới lưu TOÀN BỘ trong 1 transaction (pass 2)
public class ImportBulkContactUseCase {
    private final IContactRepository repo;
    private final ITransactionRunner txRunner;

    /** @param repo port lưu trữ @param txRunner chạy pass 2 trong 1 transaction */
    public ImportBulkContactUseCase(IContactRepository repo, ITransactionRunner txRunner) {
        this.repo = repo;
        this.txRunner = txRunner;
    }

    /**
     * Xử lý nhập hàng loạt Contact.
     * @param cmd dữ liệu import @return kết quả nhập
     */
    public ImportBulkResult execute(ImportBulkContactCommand cmd) {
        List<ImportRowError> errors = new ArrayList<>();

        // ----- Pass 1: validate mọi dòng, không chạm DB -----
        for (int i = 0; i < cmd.rows().size(); i++) {
            int rowNum = i + 2;
            ImportContactRowCommand row = cmd.rows().get(i);
            if (row.fullName() == null || row.fullName().isBlank()) {
                errors.add(new ImportRowError(rowNum, "Trường 'Họ và tên' là bắt buộc"));
                continue;
            }
            String err = ImportValidators.emailError(row.email());
            if (err == null) err = ImportValidators.phoneError(row.phone());
            if (err == null && row.gender() != null && !row.gender().isBlank() && parseGender(row.gender()) == null) {
                err = "Trường \"Giới tính\" giá trị không hợp lệ: \"" + row.gender() + "\"";
            }
            if (err != null) errors.add(new ImportRowError(rowNum, err));
        }
        if (!errors.isEmpty()) return new ImportBulkResult(0, errors.size(), errors);

        // ----- Pass 2: mọi dòng đã sạch — lưu toàn bộ trong 1 transaction -----
        int[] success = {0};
        try {
            txRunner.run(() -> {
                for (int i = 0; i < cmd.rows().size(); i++) {
                    ImportContactRowCommand row = cmd.rows().get(i);
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
                                .phone(row.phone() != null ? row.phone() : e.getPhone())
                                .source(row.source() != null ? row.source() : e.getSource())
                                .gender(gender != null ? gender : e.getGender())
                                .dateOfBirth(dateOfBirth != null ? dateOfBirth : e.getDateOfBirth())
                                .isPrimary(row.isPrimary() != null ? row.isPrimary() : e.getIsPrimary())
                                .createdAt(e.getCreatedAt()).build());
                        success[0]++;
                    // Chưa có và được phép tạo mới → thêm mới
                    } else if (isCreate) {
                        repo.save(Contact.builder()
                                .customerId(row.customerId()).assignedUserId(ownerId)
                                .salutation(row.salutation())
                                .fullName(row.fullName())
                                .title(row.title()).department(row.department())
                                .email(row.email())
                                .zalo(row.zalo()).phone(row.phone()).source(row.source())
                                .gender(gender).dateOfBirth(dateOfBirth)
                                .isPrimary(row.isPrimary() != null && row.isPrimary())
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
