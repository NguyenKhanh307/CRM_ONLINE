package vn.com.be_crm.application.order.command;

import vn.com.be_crm.application.order.dto.UpdateOrderItemCommand;
import vn.com.be_crm.application.order.dto.OrderItemResult;
import vn.com.be_crm.application.order.mapper.OrderItemCommandMapper;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.order.entity.OrderItem;
import vn.com.be_crm.domain.order.repository.IOrderItemRepository;
import vn.com.be_crm.core.error.frontend.NotFoundException;

// cập nhật dòng hàng đơn hàng. Thành tiền không lưu DB — tính lúc đọc (OrderItemCommandMapper.toResult)
public class UpdateOrderItemUseCase implements IUseCase<UpdateOrderItemCommand, OrderItemResult> {
    private final IOrderItemRepository repo;

    public UpdateOrderItemUseCase(IOrderItemRepository repo) {
        this.repo = repo;
    }

    @Override public OrderItemResult execute(UpdateOrderItemCommand cmd) {
        OrderItem existing = repo.findById(cmd.getId())
                .orElseThrow(() -> new NotFoundException("OrderItem not found: " + cmd.getId()));
        OrderItem saved = repo.save(OrderItemCommandMapper.toEntity(cmd, existing));
        return OrderItemCommandMapper.toResult(saved);
    }
}
