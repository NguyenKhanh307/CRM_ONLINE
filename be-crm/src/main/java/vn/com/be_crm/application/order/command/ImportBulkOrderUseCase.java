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
import java.util.Optional;

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
        // Duyệt từng dòng; rowNum = i + 2 vì dòng 1 là header trong file Excel
        for (int i = 0; i < cmd.rows().size(); i++) {
            int rowNum = i + 2;
            ImportOrderRowCommand row = cmd.rows().get(i);
            try {
                // Xác định owner: gán cố định theo cấu hình hoặc để null (lấy từ dòng/bản ghi cũ)
                Long ownerId = "SPECIFIC".equals(cmd.ownerMode()) ? cmd.specificOwnerId() : null;
                // Parse enum, fallback giá trị mặc định nếu ô rỗng hoặc không hợp lệ
                OrderStatus status = parseEnum(OrderStatus.class, row.status(), OrderStatus.draft);
                OrderType type = parseEnum(OrderType.class, row.orderType(), OrderType.standard);
                PaymentStatus payStatus = parseEnum(PaymentStatus.class, row.paymentStatus(), PaymentStatus.unpaid);

                // Xác định nhánh thao tác theo importType: CREATE / UPDATE / BOTH
                boolean isUpdate = "UPDATE".equals(cmd.importType()) || "BOTH".equals(cmd.importType());
                boolean isCreate = "CREATE".equals(cmd.importType()) || "BOTH".equals(cmd.importType());

                // Tìm bản ghi trùng theo mã đơn để cập nhật (chỉ khi cho phép UPDATE)
                Optional<Order> existing = Optional.empty();
                if (isUpdate && row.code() != null && !row.code().isBlank())
                    existing = repo.findByCode(row.code());

                // Có bản ghi → cập nhật (giữ field cũ, ghi đè field có trong file)
                if (existing.isPresent()) {
                    Order e = existing.get();
                    repo.save(Order.builder()
                            .id(e.getId()).code(e.getCode())
                            .customerId(e.getCustomerId()).contactId(e.getContactId())
                            .quotationId(e.getQuotationId()).opportunityId(e.getOpportunityId())
                            .ownerId(ownerId != null ? ownerId : e.getOwnerId())
                            .executorUnitId(e.getExecutorUnitId())
                            .parentOrderId(e.getParentOrderId())
                            .orderType(type)
                            .orderDate(parseDate(row.orderDate()) != null ? parseDate(row.orderDate()) : e.getOrderDate())
                            .currency(e.getCurrency()).exchangeRate(e.getExchangeRate())
                            .status(status).paymentStatus(payStatus)
                            .creditDays(e.getCreditDays()).paymentDueDate(e.getPaymentDueDate())
                            .isInvoiced(e.isInvoiced())
                            .receiverName(e.getReceiverName()).receiverPhone(e.getReceiverPhone())
                            .subtotal(row.subtotal() != null ? row.subtotal() : e.getSubtotal())
                            .discount(row.discount() != null ? row.discount() : e.getDiscount())
                            .tax(row.tax() != null ? row.tax() : e.getTax())
                            .total(row.total() != null ? row.total() : e.getTotal())
                            .note(row.note() != null ? row.note() : e.getNote())
                            .createdAt(e.getCreatedAt()).build());
                    success++;
                // Chưa có và được phép tạo mới → sinh mã tự động rồi thêm mới
                } else if (isCreate) {
                    String code = "DH-" + System.currentTimeMillis() + "-" + rowNum;
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
