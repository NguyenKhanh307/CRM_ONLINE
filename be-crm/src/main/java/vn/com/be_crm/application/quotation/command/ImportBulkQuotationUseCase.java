package vn.com.be_crm.application.quotation.command;

import vn.com.be_crm.application.quotation.dto.ImportBulkQuotationCommand;
import vn.com.be_crm.application.quotation.dto.ImportQuotationRowCommand;
import vn.com.be_crm.application.shared.dto.ImportBulkResult;
import vn.com.be_crm.application.shared.dto.ImportRowError;
import vn.com.be_crm.domain.quotation.entity.Quotation;
import vn.com.be_crm.domain.quotation.enums.QuotationStatus;
import vn.com.be_crm.domain.quotation.repository.IQuotationRepository;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/** Use case nhập hàng loạt Quotation từ file Excel/CSV. */
public class ImportBulkQuotationUseCase {
    private final IQuotationRepository repo;

    /** @param repo port lưu trữ */
    public ImportBulkQuotationUseCase(IQuotationRepository repo) { this.repo = repo; }

    /**
     * Xử lý nhập hàng loạt Quotation.
     * @param cmd dữ liệu import @return kết quả nhập
     */
    public ImportBulkResult execute(ImportBulkQuotationCommand cmd) {
        List<ImportRowError> errors = new ArrayList<>();
        int success = 0;
        for (int i = 0; i < cmd.rows().size(); i++) {
            int rowNum = i + 2;
            ImportQuotationRowCommand row = cmd.rows().get(i);
            try {
                Long ownerId = "SPECIFIC".equals(cmd.ownerMode()) ? cmd.specificOwnerId() : null;
                String code = "BG-" + System.currentTimeMillis() + "-" + rowNum;
                QuotationStatus status = parseStatus(row.status());
                repo.save(Quotation.builder()
                        .code(code).ownerId(ownerId)
                        .quoteDate(parseDate(row.quoteDate()))
                        .validUntil(parseDate(row.validUntil()))
                        .status(status != null ? status : QuotationStatus.draft)
                        .subtotal(row.subtotal()).discount(row.discount())
                        .tax(row.tax()).total(row.total())
                        .note(row.note())
                        .build());
                success++;
            } catch (Exception ex) {
                errors.add(new ImportRowError(rowNum, ex.getMessage() != null ? ex.getMessage() : "Lỗi không xác định"));
            }
        }
        return new ImportBulkResult(success, errors.size(), errors);
    }

    private QuotationStatus parseStatus(String s) {
        if (s == null || s.isBlank()) return null;
        try { return QuotationStatus.valueOf(s.trim().toLowerCase()); }
        catch (Exception e) { return null; }
    }

    private LocalDate parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        try { return LocalDate.parse(s.trim()); }
        catch (DateTimeParseException e) { return null; }
    }
}
