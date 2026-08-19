package vn.com.be_crm.application.quotation.command;

import vn.com.be_crm.application.quotation.dto.ImportBulkQuotationCommand;
import vn.com.be_crm.application.quotation.dto.ImportQuotationRowCommand;
import vn.com.be_crm.core.dto.import_.ImportBulkResult;
import vn.com.be_crm.core.dto.import_.ImportRowError;
import vn.com.be_crm.core.tx.port.ITransactionRunner;
import vn.com.be_crm.domain.quotation.entity.Quotation;
import vn.com.be_crm.domain.quotation.enums.QuotationStatus;
import vn.com.be_crm.domain.quotation.repository.IQuotationRepository;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// nhập hàng loạt Quotation từ file Excel/CSV — validate TOÀN BỘ trước (pass 1, không đụng DB),
// chỉ khi sạch hết mới lưu TOÀN BỘ trong 1 transaction (pass 2)
public class ImportBulkQuotationUseCase {
    private final IQuotationRepository repo;
    private final ITransactionRunner txRunner;

    public ImportBulkQuotationUseCase(IQuotationRepository repo, ITransactionRunner txRunner) {
        this.repo = repo;
        this.txRunner = txRunner;
    }

    public ImportBulkResult execute(ImportBulkQuotationCommand cmd) {
        List<ImportRowError> errors = new ArrayList<>();

        // ----- Pass 1: validate mọi dòng, không chạm DB -----
        for (int i = 0; i < cmd.rows().size(); i++) {
            int rowNum = i + 2; // dòng 1 là header trong file Excel
            ImportQuotationRowCommand row = cmd.rows().get(i);
            if (row.status() != null && !row.status().isBlank() && parseStatus(row.status()) == null) {
                errors.add(new ImportRowError(rowNum, "Trường \"Trạng thái\" giá trị không hợp lệ: \"" + row.status() + "\""));
            }
        }
        if (!errors.isEmpty()) return new ImportBulkResult(0, errors.size(), errors);

        // ----- Pass 2: mọi dòng đã sạch — lưu toàn bộ trong 1 transaction -----
        int[] success = {0};
        try {
            txRunner.run(() -> {
                for (int i = 0; i < cmd.rows().size(); i++) {
                    int rowNum = i + 2;
                    ImportQuotationRowCommand row = cmd.rows().get(i);
                    Long ownerId = "SPECIFIC".equals(cmd.ownerMode()) ? cmd.specificOwnerId() : null;
                    QuotationStatus status = parseStatus(row.status());

                    boolean isUpdate = "UPDATE".equals(cmd.importType()) || "BOTH".equals(cmd.importType());
                    boolean isCreate = "CREATE".equals(cmd.importType()) || "BOTH".equals(cmd.importType());

                    // tìm bản ghi trùng theo khóa duy nhất để cập nhật (chỉ khi cho phép UPDATE)
                    Optional<Quotation> existing = Optional.empty();
                    if (isUpdate && row.code() != null && !row.code().isBlank())
                        existing = repo.findByCode(row.code());

                    // có bản ghi -> cập nhật (giữ field cũ, ghi đè field có trong file)
                    if (existing.isPresent()) {
                        Quotation e = existing.get();
                        repo.save(Quotation.builder()
                                .id(e.getId()).code(e.getCode())
                                .customerId(row.customerId() != null ? row.customerId() : e.getCustomerId())
                                .contactId(row.contactId() != null ? row.contactId() : e.getContactId())
                                .opportunityId(row.opportunityId() != null ? row.opportunityId() : e.getOpportunityId())
                                .pricePolicyId(row.pricePolicyId() != null ? row.pricePolicyId() : e.getPricePolicyId())
                                .ownerId(ownerId != null ? ownerId : e.getOwnerId())
                                .quoteDate(parseDate(row.quoteDate()) != null ? parseDate(row.quoteDate()) : e.getQuoteDate())
                                .validUntil(parseDate(row.validUntil()) != null ? parseDate(row.validUntil()) : e.getValidUntil())
                                .status(status != null ? status : e.getStatus())
                                .note(row.note() != null ? row.note() : e.getNote())
                                .customerResponse(e.getCustomerResponse()).customerResponseNote(e.getCustomerResponseNote())
                                .createdAt(e.getCreatedAt()).build());
                        success[0]++;
                    // chưa có và được phép tạo mới -> thêm mới
                    } else if (isCreate) {
                        String code = "BG-" + System.currentTimeMillis() + "-" + rowNum;
                        repo.save(Quotation.builder()
                                .code(code).ownerId(ownerId)
                                .customerId(row.customerId()).contactId(row.contactId())
                                .opportunityId(row.opportunityId())
                                .pricePolicyId(row.pricePolicyId())
                                .quoteDate(parseDate(row.quoteDate()))
                                .validUntil(parseDate(row.validUntil()))
                                .status(status != null ? status : QuotationStatus.draft)
                                .note(row.note())
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
