package vn.com.be_crm.application.order.command;

import vn.com.be_crm.application.order.dto.CreateOrderItemCommand;
import vn.com.be_crm.application.order.dto.OrderItemResult;
import vn.com.be_crm.application.order.mapper.OrderItemCommandMapper;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.core.util.LineItemTotals;
import vn.com.be_crm.domain.order.entity.OrderItem;
import vn.com.be_crm.domain.order.repository.IOrderItemRepository;

/** Use case tạo mới dòng hàng đơn hàng. Thành tiền do server tính; tổng chứng từ được tính lại sau khi lưu. */
public class CreateOrderItemUseCase implements IUseCase<CreateOrderItemCommand, OrderItemResult> {
    private final IOrderItemRepository repo;
    private final RecomputeOrderTotalsUseCase recomputeUC;

    /** @param repo port lưu trữ @param recomputeUC tính lại tổng tiền chứng từ */
    public CreateOrderItemUseCase(IOrderItemRepository repo, RecomputeOrderTotalsUseCase recomputeUC) {
        this.repo = repo;
        this.recomputeUC = recomputeUC;
    }

    /** Tạo mới OrderItem (thành tiền server tính) rồi tính lại tổng chứng từ. @param cmd @return OrderItemResult */
    @Override public OrderItemResult execute(CreateOrderItemCommand cmd) {
        OrderItem e = OrderItemCommandMapper.toEntity(cmd);
        OrderItem saved = repo.save(e.toBuilder()
                .amount(LineItemTotals.lineAmount(e.getQuantity(), e.getUnitPrice(), e.getDiscount(), e.getTaxRate()))
                .build());
        recomputeUC.execute(saved.getOrderId());
        return OrderItemCommandMapper.toResult(saved);
    }
}
