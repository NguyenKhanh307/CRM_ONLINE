package vn.com.be_crm.application.invoice.command;

import vn.com.be_crm.application.invoice.dto.ImportBulkInvoiceCommand;
import vn.com.be_crm.application.invoice.dto.ImportInvoiceRowCommand;
import vn.com.be_crm.core.dto.import_.ImportBulkResult;
import vn.com.be_crm.core.dto.import_.ImportRowError;
import vn.com.be_crm.core.tx.port.ITransactionRunner;
import vn.com.be_crm.domain.invoice.entity.Invoice;
import vn.com.be_crm.domain.invoice.enums.InvoiceStatus;
import vn.com.be_crm.domain.invoice.enums.PaymentStatus;
import vn.com.be_crm.domain.invoice.repository.IInvoiceRepository;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// nhập hàng loạt Invoice từ file Excel/CSV — validate TOÀN BỘ trước (pass 1, không đụng DB), chỉ
// khi sạch hết mới lưu TOÀN BỘ trong 1 transaction (pass 2)
public class ImportBulkInvoiceUseCase {
    private final IInvoiceRepository repo;
    private final ITransactionRunner txRunner;

    public ImportBulkInvoiceUseCase(IInvoiceRepository repo, ITransactionRunner txRunner) {
        this.repo = repo;
        this.txRunner = txRunner;
    }

    public ImportBulkResult execute(ImportBulkInvoiceCommand cmd) {
        List<ImportRowError> errors = new ArrayList<>();

        // ----- Pass 1: validate mọi dòng, không chạm DB -----
        for (int i = 0; i < cmd.rows().size(); i++) {
            int rowNum = i + 2; // dòng 1 là header trong file Excel
            ImportInvoiceRowCommand row = cmd.rows().get(i);
            String err = null;
            if (!isValidEnumOrBlank(InvoiceStatus.class, row.status())) {
                err = "Trường \"Trạng thái\" giá trị không hợp lệ: \"" + row.status() + "\"";
            }
            if (err == null && !isValidEnumOrBlank(PaymentStatus.class, row.paymentStatus())) {
                err = "Trường \"Trạng thái thanh toán\" giá trị không hợp lệ: \"" + row.paymentStatus() + "\"";
            }
            if (err != null) errors.add(new ImportRowError(rowNum, err));
        }
        if (!errors.isEmpty()) return new ImportBulkResult(0, errors.size(), errors);

        // ----- Pass 2: mọi dòng đã sạch — lưu toàn bộ trong 1 transaction -----
        int[] success = {0};
        try {
            txRunner.run(() -> {
                for (int i = 0; i < cmd.rows().size(); i++) {
                    int rowNum = i + 2;
                    ImportInvoiceRowCommand row = cmd.rows().get(i);
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
                        success[0]++;
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

    private <E extends Enum<E>> E parseEnum(Class<E> cls, String val, E def) {
        if (val == null || val.isBlank()) return def;
        try { return Enum.valueOf(cls, val.trim().toLowerCase()); }
        catch (Exception e) { return def; }
    }

    // rỗng thì hợp lệ (dùng mặc định); có giá trị thì phải khớp đúng enum
    private <E extends Enum<E>> boolean isValidEnumOrBlank(Class<E> cls, String val) {
        if (val == null || val.isBlank()) return true;
        try { Enum.valueOf(cls, val.trim().toLowerCase()); return true; }
        catch (Exception e) { return false; }
    }

    private LocalDate parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        try { return LocalDate.parse(s.trim()); }
        catch (DateTimeParseException e) { return null; }
    }
}
