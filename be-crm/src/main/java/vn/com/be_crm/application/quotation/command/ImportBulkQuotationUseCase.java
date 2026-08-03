package vn.com.be_crm.application.quotation.command;

import vn.com.be_crm.application.quotation.dto.ImportBulkQuotationCommand;
import vn.com.be_crm.application.quotation.dto.ImportQuotationRowCommand;
import vn.com.be_crm.core.dto.import_.ImportBulkResult;
import vn.com.be_crm.core.dto.import_.ImportRowError;
import vn.com.be_crm.domain.quotation.entity.Quotation;
import vn.com.be_crm.domain.quotation.enums.QuotationStatus;
import vn.com.be_crm.domain.quotation.repository.IQuotationRepository;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        // Duyệt từng dòng; rowNum = i + 2 vì dòng 1 là header trong file Excel
        for (int i = 0; i < cmd.rows().size(); i++) {
            int rowNum = i + 2;
            ImportQuotationRowCommand row = cmd.rows().get(i);
            try {
                // Xác định owner: gán cố định theo cấu hình hoặc null (lấy từ dòng/bản ghi cũ)
                Long ownerId = "SPECIFIC".equals(cmd.ownerMode()) ? cmd.specificOwnerId() : null;
                QuotationStatus status = parseStatus(row.status());

                // Xác định nhánh thao tác theo importType: CREATE / UPDATE / BOTH
                boolean isUpdate = "UPDATE".equals(cmd.importType()) || "BOTH".equals(cmd.importType());
                boolean isCreate = "CREATE".equals(cmd.importType()) || "BOTH".equals(cmd.importType());

                // Tìm bản ghi trùng theo khóa duy nhất để cập nhật (chỉ khi cho phép UPDATE)
                Optional<Quotation> existing = Optional.empty();
                if (isUpdate && row.code() != null && !row.code().isBlank())
                    existing = repo.findByCode(row.code());

                // Có bản ghi → cập nhật (giữ field cũ, ghi đè field có trong file)
                if (existing.isPresent()) {
                    Quotation e = existing.get();
                    repo.save(Quotation.builder()
                            .id(e.getId()).code(e.getCode())
                            .customerId(row.customerId() != null ? row.customerId() : e.getCustomerId())
                            .contactId(row.contactId() != null ? row.contactId() : e.getContactId())
                            .opportunityId(row.opportunityId() != null ? row.opportunityId() : e.getOpportunityId())
                            .campaignId(row.campaignId() != null ? row.campaignId() : e.getCampaignId())
                            .pricePolicyId(row.pricePolicyId() != null ? row.pricePolicyId() : e.getPricePolicyId())
                            .ownerId(ownerId != null ? ownerId : e.getOwnerId())
                            .quoteDate(parseDate(row.quoteDate()) != null ? parseDate(row.quoteDate()) : e.getQuoteDate())
                            .validUntil(parseDate(row.validUntil()) != null ? parseDate(row.validUntil()) : e.getValidUntil())
                            .currency(row.currency() != null ? row.currency() : e.getCurrency())
                            .exchangeRate(row.exchangeRate() != null ? row.exchangeRate() : e.getExchangeRate())
                            .status(status != null ? status : e.getStatus())
                            .subtotal(row.subtotal() != null ? row.subtotal() : e.getSubtotal())
                            .discount(row.discount() != null ? row.discount() : e.getDiscount())
                            .tax(row.tax() != null ? row.tax() : e.getTax())
                            .total(row.total() != null ? row.total() : e.getTotal())
                            .note(row.note() != null ? row.note() : e.getNote())
                            .createdAt(e.getCreatedAt()).build());
                    success++;
                // Chưa có và được phép tạo mới → thêm mới
                } else if (isCreate) {
                    String code = "BG-" + System.currentTimeMillis() + "-" + rowNum;
                    repo.save(Quotation.builder()
                            .code(code).ownerId(ownerId)
                            .customerId(row.customerId()).contactId(row.contactId())
                            .opportunityId(row.opportunityId()).campaignId(row.campaignId())
                            .pricePolicyId(row.pricePolicyId())
                            .quoteDate(parseDate(row.quoteDate()))
                            .validUntil(parseDate(row.validUntil()))
                            .currency(row.currency() != null ? row.currency() : "VND")
                            .exchangeRate(row.exchangeRate() != null ? row.exchangeRate() : java.math.BigDecimal.ONE)
                            .status(status != null ? status : QuotationStatus.draft)
                            .subtotal(row.subtotal()).discount(row.discount())
                            .tax(row.tax()).total(row.total())
                            .note(row.note())
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
