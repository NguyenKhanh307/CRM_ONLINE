package vn.com.be_crm.application.invoice.command;

import vn.com.be_crm.application.invoice.dto.ImportBulkInvoiceCommand;
import vn.com.be_crm.application.invoice.dto.ImportInvoiceRowCommand;
import vn.com.be_crm.application.shared.dto.ImportBulkResult;
import vn.com.be_crm.application.shared.dto.ImportRowError;
import vn.com.be_crm.domain.invoice.entity.Invoice;
import vn.com.be_crm.domain.invoice.enums.InvoiceStatus;
import vn.com.be_crm.domain.invoice.enums.PaymentStatus;
import vn.com.be_crm.domain.invoice.repository.IInvoiceRepository;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Use case nhập hàng loạt Invoice từ file Excel/CSV. */
public class ImportBulkInvoiceUseCase {
    private final IInvoiceRepository repo;

    /** @param repo port lưu trữ */
    public ImportBulkInvoiceUseCase(IInvoiceRepository repo) { this.repo = repo; }

    /**
     * Xử lý nhập hàng loạt Invoice.
     * @param cmd dữ liệu import @return kết quả nhập
     */
    public ImportBulkResult execute(ImportBulkInvoiceCommand cmd) {
        List<ImportRowError> errors = new ArrayList<>();
        int success = 0;
        // Duyệt từng dòng; rowNum = i + 2 vì dòng 1 là header trong file Excel
        for (int i = 0; i < cmd.rows().size(); i++) {
            int rowNum = i + 2;
            ImportInvoiceRowCommand row = cmd.rows().get(i);
            try {
                // Xác định owner: gán cố định theo cấu hình hoặc để null (lấy từ dòng/bản ghi cũ)
                Long ownerId = "SPECIFIC".equals(cmd.ownerMode()) ? cmd.specificOwnerId() : null;
                // Parse enum, fallback giá trị mặc định nếu ô rỗng hoặc không hợp lệ
                InvoiceStatus status = parseEnum(InvoiceStatus.class, row.status(), InvoiceStatus.draft);
                PaymentStatus payStatus = parseEnum(PaymentStatus.class, row.paymentStatus(), PaymentStatus.unpaid);

                // Xác định nhánh thao tác theo importType: CREATE / UPDATE / BOTH
                boolean isUpdate = "UPDATE".equals(cmd.importType()) || "BOTH".equals(cmd.importType());
                boolean isCreate = "CREATE".equals(cmd.importType()) || "BOTH".equals(cmd.importType());

                // Tìm bản ghi trùng theo mã hóa đơn để cập nhật (chỉ khi cho phép UPDATE)
                Optional<Invoice> existing = Optional.empty();
                if (isUpdate && row.code() != null && !row.code().isBlank())
                    existing = repo.findByCode(row.code());

                // Có bản ghi → cập nhật (giữ field cũ, ghi đè field có trong file)
                if (existing.isPresent()) {
                    Invoice e = existing.get();
                    repo.save(e.toBuilder()
                            .customerId(row.customerId() != null ? row.customerId() : e.getCustomerId())
                            .contactId(row.contactId() != null ? row.contactId() : e.getContactId())
                            .quotationId(row.quotationId() != null ? row.quotationId() : e.getQuotationId())
                            .opportunityId(row.opportunityId() != null ? row.opportunityId() : e.getOpportunityId())
                            .orderId(row.orderId() != null ? row.orderId() : e.getOrderId())
                            .campaignId(row.campaignId() != null ? row.campaignId() : e.getCampaignId())
                            .ownerId(ownerId != null ? ownerId : e.getOwnerId())
                            .invoiceDate(parseDate(row.invoiceDate()) != null ? parseDate(row.invoiceDate()) : e.getInvoiceDate())
                            .dueDate(parseDate(row.dueDate()) != null ? parseDate(row.dueDate()) : e.getDueDate())
                            .currency(row.currency() != null ? row.currency() : e.getCurrency())
                            .exchangeRate(row.exchangeRate() != null ? row.exchangeRate() : e.getExchangeRate())
                            .status(status).paymentStatus(payStatus)
                            .billingAddress(row.billingAddress() != null ? row.billingAddress() : e.getBillingAddress())
                            .taxCode(row.taxCode() != null ? row.taxCode() : e.getTaxCode())
                            .subtotal(row.subtotal() != null ? row.subtotal() : e.getSubtotal())
                            .discount(row.discount() != null ? row.discount() : e.getDiscount())
                            .tax(row.tax() != null ? row.tax() : e.getTax())
                            .total(row.total() != null ? row.total() : e.getTotal())
                            .note(row.note() != null ? row.note() : e.getNote())
                            .build());
                    success++;
                // Chưa có và được phép tạo mới → sinh mã tự động rồi thêm mới
                } else if (isCreate) {
                    String code = "HD-" + System.currentTimeMillis() + "-" + rowNum;
                    repo.save(Invoice.builder()
                            .code(code).ownerId(ownerId)
                            .customerId(row.customerId()).contactId(row.contactId())
                            .quotationId(row.quotationId()).opportunityId(row.opportunityId())
                            .orderId(row.orderId()).campaignId(row.campaignId())
                            .invoiceDate(parseDate(row.invoiceDate()))
                            .dueDate(parseDate(row.dueDate()))
                            .currency(row.currency() != null ? row.currency() : "VND")
                            .exchangeRate(row.exchangeRate() != null ? row.exchangeRate() : java.math.BigDecimal.ONE)
                            .status(status).paymentStatus(payStatus)
                            .billingAddress(row.billingAddress()).taxCode(row.taxCode())
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

    private <E extends Enum<E>> E parseEnum(Class<E> cls, String val, E def) {
        if (val == null || val.isBlank()) return def;
        try { return Enum.valueOf(cls, val.trim().toLowerCase()); }
        catch (Exception e) { return def; }
    }

    private LocalDate parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        try { return LocalDate.parse(s.trim()); }
        catch (DateTimeParseException e) { return null; }
    }
}
