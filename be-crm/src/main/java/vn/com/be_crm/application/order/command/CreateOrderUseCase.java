package vn.com.be_crm.application.order.command;

import vn.com.be_crm.application.order.dto.CreateOrderCommand;
import vn.com.be_crm.application.order.dto.OrderResult;
import vn.com.be_crm.application.order.mapper.OrderCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.order.repository.IOrderRepository;

/** Use case tạo mới đơn hàng. */
public class CreateOrderUseCase implements IUseCase<CreateOrderCommand, OrderResult> {
    private final IOrderRepository repo;
    /** @param repo port lưu trữ */
    public CreateOrderUseCase(IOrderRepository repo) { this.repo = repo; }
    /** Tạo mới Order. @param cmd @return OrderResult */
    @Override public OrderResult execute(CreateOrderCommand cmd) {
        return OrderCommandMapper.toResult(repo.save(OrderCommandMapper.toEntity(cmd)));
    }
}
