package vn.com.be_crm.application.order.command;

import vn.com.be_crm.application.order.dto.OrderResult;
import vn.com.be_crm.application.order.dto.UpdateOrderCommand;
import vn.com.be_crm.application.order.mapper.OrderCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.order.entity.Order;
import vn.com.be_crm.domain.order.repository.IOrderRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case cập nhật đơn hàng. */
public class UpdateOrderUseCase implements IUseCase<UpdateOrderCommand, OrderResult> {
    private final IOrderRepository repo;
    /** @param repo port lưu trữ */
    public UpdateOrderUseCase(IOrderRepository repo) { this.repo = repo; }
    /** Cập nhật Order. @param cmd @return OrderResult @throws NotFoundException */
    @Override public OrderResult execute(UpdateOrderCommand cmd) {
        Order e = repo.findById(cmd.getId()).orElseThrow(() -> new NotFoundException("Order not found: " + cmd.getId()));
        return OrderCommandMapper.toResult(repo.save(OrderCommandMapper.toEntity(cmd, e)));
    }
}
