package vn.com.be_crm.application.order.command;

import vn.com.be_crm.application.order.dto.ImportBulkOrderCommand;
import vn.com.be_crm.application.order.dto.ImportOrderRowCommand;
import vn.com.be_crm.application.shared.dto.ImportBulkResult;
import vn.com.be_crm.application.shared.dto.ImportRowError;
import vn.com.be_crm.domain.order.entity.Order;
import vn.com.be_crm.domain.order.enums.OrderStatus;
import vn.com.be_crm.domain.order.enums.OrderType;
import vn.com.be_crm.domain.order.enums.PaymentStatus;
import vn.com.be_crm.domain.order.repository.IOrderRepository;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/** Use case nhập hàng loạt Order từ file Excel/CSV. */
public class ImportBulkOrderUseCase {
    private final IOrderRepository repo;

    /** @param repo port lưu trữ */
    public ImportBulkOrderUseCase(IOrderRepository repo) { this.repo = repo; }

    /**
     * Xử lý nhập hàng loạt Order.
     * @param cmd dữ liệu import @return kết quả nhập
     */
    public ImportBulkResult execute(ImportBulkOrderCommand cmd) {
        List<ImportRowError> errors = new ArrayList<>();
        int success = 0;
        for (int i = 0; i < cmd.rows().size(); i++) {
            int rowNum = i + 2;
            ImportOrderRowCommand row = cmd.rows().get(i);
            try {
                Long ownerId = "SPECIFIC".equals(cmd.ownerMode()) ? cmd.specificOwnerId() : null;
                String code = "DH-" + System.currentTimeMillis() + "-" + rowNum;
                OrderStatus status = parseEnum(OrderStatus.class, row.status(), OrderStatus.draft);
                OrderType type = parseEnum(OrderType.class, row.orderType(), OrderType.standard);
                PaymentStatus payStatus = parseEnum(PaymentStatus.class, row.paymentStatus(), PaymentStatus.unpaid);
                repo.save(Order.builder()
                        .code(code).ownerId(ownerId)
                        .orderType(type)
                        .orderDate(parseDate(row.orderDate()))
                        .status(status).paymentStatus(payStatus)
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
