package vn.com.be_crm.application.order.command;

import vn.com.be_crm.application.order.dto.ImportBulkOrderCommand;
import vn.com.be_crm.application.order.dto.ImportOrderRowCommand;
import vn.com.be_crm.core.dto.import_.ImportBulkResult;
import vn.com.be_crm.core.dto.import_.ImportRowError;
import vn.com.be_crm.domain.order.entity.Order;
import vn.com.be_crm.domain.order.enums.OrderStatus;
import vn.com.be_crm.domain.order.repository.IOrderRepository;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// nhập hàng loạt Order từ file Excel/CSV — mỗi dòng xử lý độc lập, lỗi từng dòng được thu thập,
// không dừng toàn bộ lô
public class ImportBulkOrderUseCase {
    private final IOrderRepository repo;

    public ImportBulkOrderUseCase(IOrderRepository repo) { this.repo = repo; }

    public ImportBulkResult execute(ImportBulkOrderCommand cmd) {
        List<ImportRowError> errors = new ArrayList<>();
        int success = 0;
        for (int i = 0; i < cmd.rows().size(); i++) {
            int rowNum = i + 2; // dòng 1 là header trong file Excel
            ImportOrderRowCommand row = cmd.rows().get(i);
            try {
                Long ownerId = "SPECIFIC".equals(cmd.ownerMode()) ? cmd.specificOwnerId() : null;
                OrderStatus status = parseEnum(OrderStatus.class, row.status(), OrderStatus.draft);

                boolean isUpdate = "UPDATE".equals(cmd.importType()) || "BOTH".equals(cmd.importType());
                boolean isCreate = "CREATE".equals(cmd.importType()) || "BOTH".equals(cmd.importType());

                // tìm bản ghi trùng theo mã đơn hàng để cập nhật (chỉ khi cho phép UPDATE)
                Optional<Order> existing = Optional.empty();
                if (isUpdate && row.code() != null && !row.code().isBlank())
                    existing = repo.findByCode(row.code());

                // có bản ghi -> cập nhật (giữ field cũ, ghi đè field có trong file)
                if (existing.isPresent()) {
                    Order e = existing.get();
                    repo.save(e.toBuilder()
                            .quotationId(row.quotationId() != null ? row.quotationId() : e.getQuotationId())
                            .ownerId(ownerId != null ? ownerId : e.getOwnerId())
                            .orderDate(parseDate(row.orderDate()) != null ? parseDate(row.orderDate()) : e.getOrderDate())
                            .deliveryDate(parseDate(row.deliveryDate()) != null ? parseDate(row.deliveryDate()) : e.getDeliveryDate())
                            .status(status)
                            .note(row.note() != null ? row.note() : e.getNote())
                            .build());
                    success++;
                // chưa có và được phép tạo mới -> sinh mã tự động rồi thêm mới
                } else if (isCreate) {
                    String code = "DH-" + System.currentTimeMillis() + "-" + rowNum;
                    repo.save(Order.builder()
                            .code(code).ownerId(ownerId)
                            .quotationId(row.quotationId())
                            .orderDate(parseDate(row.orderDate()))
                            .deliveryDate(parseDate(row.deliveryDate()))
                            .status(status)
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
