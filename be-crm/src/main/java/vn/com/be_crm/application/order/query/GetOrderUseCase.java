package vn.com.be_crm.application.order.query;

import vn.com.be_crm.application.order.dto.OrderResult;
import vn.com.be_crm.application.order.mapper.OrderCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.order.repository.IOrderRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case lấy đơn hàng theo ID. */
public class GetOrderUseCase implements IUseCase<Long, OrderResult> {
    private final IOrderRepository repo;
    /** @param repo port lưu trữ */
    public GetOrderUseCase(IOrderRepository repo) { this.repo = repo; }
    /** Lấy Order theo ID. @param id @return OrderResult @throws NotFoundException */
    @Override public OrderResult execute(Long id) {
        return OrderCommandMapper.toResult(repo.findById(id).orElseThrow(() -> new NotFoundException("Order not found: " + id)));
    }
}
