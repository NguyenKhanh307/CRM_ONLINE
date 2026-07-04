package vn.com.be_crm.application.order.command;

import vn.com.be_crm.application.order.dto.OrderItemResult;
import vn.com.be_crm.application.order.dto.UpdateOrderItemCommand;
import vn.com.be_crm.application.order.mapper.OrderItemCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.order.entity.OrderItem;
import vn.com.be_crm.domain.order.repository.IOrderItemRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case cập nhật dòng đơn hàng. */
public class UpdateOrderItemUseCase implements IUseCase<UpdateOrderItemCommand, OrderItemResult> {
    private final IOrderItemRepository repo;
    /** @param repo port lưu trữ */
    public UpdateOrderItemUseCase(IOrderItemRepository repo) { this.repo = repo; }
    /** Cập nhật OrderItem. @param cmd @return OrderItemResult @throws NotFoundException */
    @Override public OrderItemResult execute(UpdateOrderItemCommand cmd) {
        OrderItem e = repo.findById(cmd.getId()).orElseThrow(() -> new NotFoundException("OrderItem not found: " + cmd.getId()));
        return OrderItemCommandMapper.toResult(repo.save(OrderItemCommandMapper.toEntity(cmd, e)));
    }
}
