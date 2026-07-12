package vn.com.be_crm.application.order.command;

import vn.com.be_crm.application.order.dto.UpdateOrderItemCommand;
import vn.com.be_crm.application.order.dto.OrderItemResult;
import vn.com.be_crm.application.order.mapper.OrderItemCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.application.shared.util.LineItemTotals;
import vn.com.be_crm.domain.order.entity.OrderItem;
import vn.com.be_crm.domain.order.repository.IOrderItemRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case cập nhật dòng hàng đơn hàng. Thành tiền do server tính; tổng chứng từ được tính lại sau khi lưu. */
public class UpdateOrderItemUseCase implements IUseCase<UpdateOrderItemCommand, OrderItemResult> {
    private final IOrderItemRepository repo;
    private final RecomputeOrderTotalsUseCase recomputeUC;

    /** @param repo port lưu trữ @param recomputeUC tính lại tổng tiền chứng từ */
    public UpdateOrderItemUseCase(IOrderItemRepository repo, RecomputeOrderTotalsUseCase recomputeUC) {
        this.repo = repo;
        this.recomputeUC = recomputeUC;
    }

    /** Cập nhật OrderItem (thành tiền server tính) rồi tính lại tổng chứng từ. @param cmd @return OrderItemResult @throws NotFoundException */
    @Override public OrderItemResult execute(UpdateOrderItemCommand cmd) {
        OrderItem existing = repo.findById(cmd.getId())
                .orElseThrow(() -> new NotFoundException("OrderItem not found: " + cmd.getId()));
        OrderItem merged = OrderItemCommandMapper.toEntity(cmd, existing);
        OrderItem saved = repo.save(merged.toBuilder()
                .amount(LineItemTotals.lineAmount(merged.getQuantity(), merged.getUnitPrice(),
                        merged.getDiscount(), merged.getTaxRate()))
                .build());
        recomputeUC.execute(saved.getOrderId());
        return OrderItemCommandMapper.toResult(saved);
    }
}
