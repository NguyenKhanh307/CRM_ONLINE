package vn.com.be_crm.application.order.query;

import vn.com.be_crm.application.order.dto.OrderItemResult;
import vn.com.be_crm.application.order.mapper.OrderItemCommandMapper;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.order.repository.IOrderItemRepository;

import java.util.List;
import java.util.stream.Collectors;

/** Use case lấy danh sách dòng đơn hàng theo orderId. */
public class ListOrderItemUseCase implements IUseCase<Long, List<OrderItemResult>> {
    private final IOrderItemRepository repo;
    /** @param repo port lưu trữ */
    public ListOrderItemUseCase(IOrderItemRepository repo) { this.repo = repo; }
    /** Lấy danh sách OrderItem theo orderId. @param orderId @return danh sách */
    @Override public List<OrderItemResult> execute(Long orderId) {
        return repo.findAllByOrderId(orderId).stream().map(OrderItemCommandMapper::toResult).collect(Collectors.toList());
    }
}
