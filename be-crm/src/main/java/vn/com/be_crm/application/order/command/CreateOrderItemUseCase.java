package vn.com.be_crm.application.order.command;

import vn.com.be_crm.application.order.dto.CreateOrderItemCommand;
import vn.com.be_crm.application.order.dto.OrderItemResult;
import vn.com.be_crm.application.order.mapper.OrderItemCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.order.repository.IOrderItemRepository;

/** Use case tạo mới dòng đơn hàng. */
public class CreateOrderItemUseCase implements IUseCase<CreateOrderItemCommand, OrderItemResult> {
    private final IOrderItemRepository repo;
    /** @param repo port lưu trữ */
    public CreateOrderItemUseCase(IOrderItemRepository repo) { this.repo = repo; }
    /** Tạo mới OrderItem. @param cmd @return OrderItemResult */
    @Override public OrderItemResult execute(CreateOrderItemCommand cmd) {
        return OrderItemCommandMapper.toResult(repo.save(OrderItemCommandMapper.toEntity(cmd)));
    }
}
