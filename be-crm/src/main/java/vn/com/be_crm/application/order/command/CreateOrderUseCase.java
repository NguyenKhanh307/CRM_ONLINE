package vn.com.be_crm.application.order.command;

import vn.com.be_crm.application.order.dto.CreateOrderCommand;
import vn.com.be_crm.application.order.dto.OrderResult;
import vn.com.be_crm.application.order.mapper.OrderCommandMapper;
import vn.com.be_crm.application.order.mapper.OrderItemCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.order.repository.IOrderRepository;

import java.util.stream.Collectors;

/** Use case tạo mới đơn hàng (kèm dòng hàng nếu có). */
public class CreateOrderUseCase implements IUseCase<CreateOrderCommand, OrderResult> {
    private final IOrderRepository repo;
    /** @param repo port lưu trữ */
    public CreateOrderUseCase(IOrderRepository repo) { this.repo = repo; }
    /** Tạo mới Order; nếu có items thì lưu header + dòng hàng trong một transaction. @param cmd @return OrderResult */
    @Override public OrderResult execute(CreateOrderCommand cmd) {
        var entity = OrderCommandMapper.toEntity(cmd);
        if (cmd.getItems() != null && !cmd.getItems().isEmpty()) {
            var items = cmd.getItems().stream()
                    .map(OrderItemCommandMapper::toEntity).collect(Collectors.toList());
            return OrderCommandMapper.toResult(repo.saveWithItems(entity, items));
        }
        return OrderCommandMapper.toResult(repo.save(entity));
    }
}
