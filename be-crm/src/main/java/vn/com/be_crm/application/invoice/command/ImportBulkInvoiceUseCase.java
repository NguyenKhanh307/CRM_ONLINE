package vn.com.be_crm.application.invoice.command;

import vn.com.be_crm.application.invoice.dto.ImportBulkInvoiceCommand;
import vn.com.be_crm.application.invoice.dto.ImportInvoiceRowCommand;
import vn.com.be_crm.core.dto.import_.ImportBulkResult;
import vn.com.be_crm.core.dto.import_.ImportRowError;
import vn.com.be_crm.domain.invoice.entity.Invoice;
import vn.com.be_crm.domain.invoice.enums.InvoiceStatus;
import vn.com.be_crm.domain.invoice.enums.PaymentStatus;
import vn.com.be_crm.domain.invoice.repository.IInvoiceRepository;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// nhập hàng loạt Invoice từ file Excel/CSV — mỗi dòng xử lý độc lập, lỗi từng dòng được thu
// thập, không dừng toàn bộ lô
public class ImportBulkInvoiceUseCase {
    private final IInvoiceRepository repo;

    public ImportBulkInvoiceUseCase(IInvoiceRepository repo) { this.repo = repo; }

    public ImportBulkResult execute(ImportBulkInvoiceCommand cmd) {
        List<ImportRowError> errors = new ArrayList<>();
        int success = 0;
        for (int i = 0; i < cmd.rows().size(); i++) {
            int rowNum = i + 2; // dòng 1 là header trong file Excel
            ImportInvoiceRowCommand row = cmd.rows().get(i);
            try {
                Long ownerId = "SPECIFIC".equals(cmd.ownerMode()) ? cmd.specificOwnerId() : null;
                InvoiceStatus status = parseEnum(InvoiceStatus.class, row.status(), InvoiceStatus.draft);
                PaymentStatus payStatus = parseEnum(PaymentStatus.class, row.paymentStatus(), PaymentStatus.unpaid);

                boolean isUpdate = "UPDATE".equals(cmd.importType()) || "BOTH".equals(cmd.importType());
                boolean isCreate = "CREATE".equals(cmd.importType()) || "BOTH".equals(cmd.importType());

                // tìm bản ghi trùng theo mã hóa đơn để cập nhật (chỉ khi cho phép UPDATE)
                Optional<Invoice> existing = Optional.empty();
                if (isUpdate && row.code() != null && !row.code().isBlank())
                    existing = repo.findByCode(row.code());

                // có bản ghi -> cập nhật (giữ field cũ, ghi đè field có trong file)
                if (existing.isPresent()) {
                    Invoice e = existing.get();
                    repo.save(e.toBuilder()
                            .orderId(row.orderId() != null ? row.orderId() : e.getOrderId())
                            .ownerId(ownerId != null ? ownerId : e.getOwnerId())
                            .invoiceDate(parseDate(row.invoiceDate()) != null ? parseDate(row.invoiceDate()) : e.getInvoiceDate())
                            .dueDate(parseDate(row.dueDate()) != null ? parseDate(row.dueDate()) : e.getDueDate())
                            .status(status).paymentStatus(payStatus)
                            .note(row.note() != null ? row.note() : e.getNote())
                            .build());
                    success++;
                // chưa có và được phép tạo mới -> sinh mã tự động rồi thêm mới
                } else if (isCreate) {
                    String code = "HD-" + System.currentTimeMillis() + "-" + rowNum;
                    repo.save(Invoice.builder()
                            .code(code).ownerId(ownerId)
                            .orderId(row.orderId())
                            .invoiceDate(parseDate(row.invoiceDate()))
                            .dueDate(parseDate(row.dueDate()))
                            .status(status).paymentStatus(payStatus)
                            .note(row.note())
                            .build());
                    success++;
                }
            } catch (Exception ex) {
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
