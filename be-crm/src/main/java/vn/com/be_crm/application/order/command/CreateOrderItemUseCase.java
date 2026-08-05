package vn.com.be_crm.application.order.command;

import vn.com.be_crm.application.order.dto.CreateOrderItemCommand;
import vn.com.be_crm.application.order.dto.OrderItemResult;
import vn.com.be_crm.application.order.mapper.OrderItemCommandMapper;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.order.entity.OrderItem;
import vn.com.be_crm.domain.order.repository.IOrderItemRepository;

// tạo mới dòng hàng đơn hàng. Thành tiền không lưu DB — tính lúc đọc (OrderItemCommandMapper.toResult)
public class CreateOrderItemUseCase implements IUseCase<CreateOrderItemCommand, OrderItemResult> {
    private final IOrderItemRepository repo;

    public CreateOrderItemUseCase(IOrderItemRepository repo) {
        this.repo = repo;
    }

    @Override public OrderItemResult execute(CreateOrderItemCommand cmd) {
        OrderItem saved = repo.save(OrderItemCommandMapper.toEntity(cmd));
        return OrderItemCommandMapper.toResult(saved);
    }
}
