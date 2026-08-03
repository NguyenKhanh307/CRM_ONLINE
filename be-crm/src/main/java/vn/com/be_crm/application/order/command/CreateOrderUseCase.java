package vn.com.be_crm.application.order.command;

import vn.com.be_crm.core.util.CrossFieldRules;
import vn.com.be_crm.application.order.dto.CreateOrderCommand;
import vn.com.be_crm.application.order.dto.OrderResult;
import vn.com.be_crm.application.order.mapper.OrderCommandMapper;
import vn.com.be_crm.application.order.mapper.OrderItemCommandMapper;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.core.error.frontend.DomainException;
import vn.com.be_crm.core.util.LineItemTotals;
import vn.com.be_crm.domain.order.entity.OrderItem;
import vn.com.be_crm.domain.order.repository.IOrderRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/** Use case tạo mới đơn hàng (kèm dòng hàng nếu có). */
public class CreateOrderUseCase implements IUseCase<CreateOrderCommand, OrderResult> {
    private final IOrderRepository repo;
    /** @param repo port lưu trữ */
    public CreateOrderUseCase(IOrderRepository repo) { this.repo = repo; }

    /**
     * Tạo mới Order; nếu có items thì lưu header + dòng hàng trong một transaction.
     * Thành tiền từng dòng và tổng tiền chứng từ do SERVER tính lại — bỏ qua số tiền client gửi lên.
     * @param cmd @return OrderResult
     */
    @Override public OrderResult execute(CreateOrderCommand cmd) {
        // Ràng buộc khoảng thời gian: ngày giao hàng không được trước ngày đặt hàng
        CrossFieldRules.requireDateRange(cmd.getOrderDate(), cmd.getDeliveryDate(), "Ngày đặt hàng", "Ngày giao hàng");
        // Check trùng thân thiện trước khi insert (DB unique constraint là lớp phòng thủ 2)
        if (cmd.getCode() != null && repo.findByCode(cmd.getCode()).isPresent()) {
            throw new DomainException("Mã đơn hàng \"" + cmd.getCode() + "\" đã tồn tại, vui lòng dùng mã khác");
        }
        var entity = OrderCommandMapper.toEntity(cmd);
        if (cmd.getItems() != null && !cmd.getItems().isEmpty()) {
            List<OrderItem> items = cmd.getItems().stream()
                    .map(OrderItemCommandMapper::toEntity)
                    .map(CreateOrderUseCase::withComputedAmount)
                    .collect(Collectors.toList());
            var totals = LineItemTotals.compute(items, OrderItem::getQuantity, OrderItem::getUnitPrice,
                    OrderItem::getDiscount, OrderItem::getTaxRate);
            entity = entity.toBuilder()
                    .subtotal(totals.subtotal()).discount(totals.discount())
                    .tax(totals.tax()).total(totals.total())
                    .build();
            return OrderCommandMapper.toResult(repo.saveWithItems(entity, items));
        }
        entity = entity.toBuilder()
                .subtotal(BigDecimal.ZERO).discount(BigDecimal.ZERO)
                .tax(BigDecimal.ZERO).total(BigDecimal.ZERO)
                .build();
        return OrderCommandMapper.toResult(repo.save(entity));
    }

    /** Ghi đè thành tiền của dòng hàng bằng giá trị server tính. */
    private static OrderItem withComputedAmount(OrderItem i) {
        return i.toBuilder()
                .amount(LineItemTotals.lineAmount(i.getQuantity(), i.getUnitPrice(), i.getDiscount(), i.getTaxRate()))
                .build();
    }
}
