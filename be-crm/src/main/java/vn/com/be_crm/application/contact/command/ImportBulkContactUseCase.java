package vn.com.be_crm.application.contact.command;

import vn.com.be_crm.application.contact.dto.ImportBulkContactCommand;
import vn.com.be_crm.application.contact.dto.ImportContactRowCommand;
import vn.com.be_crm.application.shared.dto.ImportBulkResult;
import vn.com.be_crm.application.shared.dto.ImportRowError;
import vn.com.be_crm.domain.contact.entity.Contact;
import vn.com.be_crm.domain.contact.repository.IContactRepository;

import java.util.ArrayList;
import java.util.List;

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
        for (int i = 0; i < cmd.rows().size(); i++) {
            int rowNum = i + 2;
            ImportContactRowCommand row = cmd.rows().get(i);
            try {
                if (row.fullName() == null || row.fullName().isBlank()) {
                    errors.add(new ImportRowError(rowNum, "Trường 'Họ và tên' là bắt buộc"));
                    continue;
                }
                repo.save(Contact.builder()
                        .fullName(row.fullName())
                        .position(row.position())
                        .email(row.email())
                        .address(row.address())
                        .isPrimary(false)
                        .build());
                success++;
            } catch (Exception ex) {
                errors.add(new ImportRowError(rowNum, ex.getMessage() != null ? ex.getMessage() : "Lỗi không xác định"));
            }
        }
        return new ImportBulkResult(success, errors.size(), errors);
    }
}
