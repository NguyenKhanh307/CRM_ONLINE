package vn.com.be_crm.application.order.command;

import vn.com.be_crm.core.util.LineItemTotals;
import vn.com.be_crm.domain.order.entity.Order;
import vn.com.be_crm.domain.order.entity.OrderItem;
import vn.com.be_crm.domain.order.repository.IOrderItemRepository;
import vn.com.be_crm.domain.order.repository.IOrderRepository;

/**
 * Use case tính lại tổng tiền đơn hàng TỪ dòng hàng (server là nguồn chân lý, không tin số client gửi).
 * Gọi sau mỗi lần tạo/sửa/xóa dòng hàng đơn hàng và sau khi cập nhật header.
 */
public class RecomputeOrderTotalsUseCase {
    private final IOrderRepository repo;
    private final IOrderItemRepository itemRepo;

    /** @param repo đơn hàng @param itemRepo dòng hàng đơn hàng */
    public RecomputeOrderTotalsUseCase(IOrderRepository repo, IOrderItemRepository itemRepo) {
        this.repo = repo;
        this.itemRepo = itemRepo;
    }

    /**
     * Tính lại subtotal/discount/tax/total của đơn hàng theo dòng hàng hiện có rồi lưu.
     * @param orderId ID đơn hàng
     */
    public void execute(Long orderId) {
        if (orderId == null) return;
        Order o = repo.findById(orderId).orElse(null);
        if (o == null) return;
        LineItemTotals.Totals t = LineItemTotals.compute(
                itemRepo.findAllByOrderId(orderId),
                OrderItem::getQuantity, OrderItem::getUnitPrice,
                OrderItem::getDiscount, OrderItem::getTaxRate);
        repo.save(o.toBuilder()
                .subtotal(t.subtotal()).discount(t.discount()).tax(t.tax()).total(t.total())
                .build());
    }
}
