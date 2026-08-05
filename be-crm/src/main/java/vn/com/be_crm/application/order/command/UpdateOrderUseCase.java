package vn.com.be_crm.application.order.command;

import vn.com.be_crm.core.util.CrossFieldRules;
import vn.com.be_crm.application.order.dto.OrderResult;
import vn.com.be_crm.application.order.dto.UpdateOrderCommand;
import vn.com.be_crm.application.order.mapper.OrderCommandMapper;
import vn.com.be_crm.core.notify.NotifyAssignmentUseCase;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.core.util.LineItemTotals;
import vn.com.be_crm.domain.order.entity.Order;
import vn.com.be_crm.domain.order.entity.OrderItem;
import vn.com.be_crm.domain.order.repository.IOrderItemRepository;
import vn.com.be_crm.domain.order.repository.IOrderRepository;
import vn.com.be_crm.core.error.frontend.DomainException;
import vn.com.be_crm.core.error.frontend.NotFoundException;

import java.util.List;
import java.util.Objects;

// cập nhật đơn hàng; chặn sửa khi đã khóa (đã xuất hóa đơn)
public class UpdateOrderUseCase implements IUseCase<UpdateOrderCommand, OrderResult> {
    private final IOrderRepository repo;
    private final IOrderItemRepository itemRepo;
    private final NotifyAssignmentUseCase notifyUC;

    public UpdateOrderUseCase(IOrderRepository repo, IOrderItemRepository itemRepo,
                              NotifyAssignmentUseCase notifyUC) {
        this.repo = repo;
        this.itemRepo = itemRepo;
        this.notifyUC = notifyUC;
    }

    @Override public OrderResult execute(UpdateOrderCommand cmd) {
        // ràng buộc khoảng thời gian: ngày giao hàng không được trước ngày đặt hàng
        CrossFieldRules.requireDateRange(cmd.getOrderDate(), cmd.getDeliveryDate(), "Ngày đặt hàng", "Ngày giao hàng");
        Order e = repo.findById(cmd.getId()).orElseThrow(() -> new NotFoundException("Order not found: " + cmd.getId()));
        if (e.isLocked()) throw new DomainException("Đơn hàng đã khóa (đã xuất hóa đơn), không thể sửa");
        Order saved = repo.save(OrderCommandMapper.toEntity(cmd, e));

        OrderResult result = OrderCommandMapper.toResult(saved);
        List<OrderItem> items = itemRepo.findAllByOrderId(saved.getId());
        LineItemTotals.Totals t = LineItemTotals.compute(items,
                OrderItem::getQuantity, OrderItem::getUnitPrice, OrderItem::getDiscount, OrderItem::getTaxRate);
        result.setSubtotal(t.subtotal()); result.setDiscount(t.discount());
        result.setTax(t.tax()); result.setTotal(t.total());

        // đổi người phụ trách -> báo cho người nhận việc
        if (!Objects.equals(e.getOwnerId(), saved.getOwnerId())) {
            notifyUC.notifyAssigned("order", "đơn hàng", saved.getOwnerId(), saved.getId(), saved.getCode());
        }
        return result;
    }
}
