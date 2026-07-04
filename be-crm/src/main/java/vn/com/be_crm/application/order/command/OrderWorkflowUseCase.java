package vn.com.be_crm.application.order.command;

import vn.com.be_crm.application.order.dto.OrderResult;
import vn.com.be_crm.application.order.mapper.OrderCommandMapper;
import vn.com.be_crm.domain.order.entity.Order;
import vn.com.be_crm.domain.order.enums.OrderStatus;
import vn.com.be_crm.domain.order.repository.IOrderRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/**
 * Use case điều phối trạng thái đơn hàng (theo hành động, không sửa tay):
 * confirm (draft → confirmed) / process (confirmed → processing) /
 * complete (→ completed) / cancel (→ cancelled). Mỗi bước có guard chuyển trạng thái.
 */
public class OrderWorkflowUseCase {
    private final IOrderRepository orderRepo;

    /** @param orderRepo port lưu trữ đơn hàng */
    public OrderWorkflowUseCase(IOrderRepository orderRepo) {
        this.orderRepo = orderRepo;
    }

    /**
     * Xác nhận đơn hàng (draft → confirmed).
     * @param id ID đơn hàng @return đơn hàng sau cập nhật
     */
    public OrderResult confirm(Long id) {
        Order o = load(id);
        o.getStatus().ensureCanTransitionTo(OrderStatus.confirmed);
        return OrderCommandMapper.toResult(orderRepo.save(o.toBuilder().status(OrderStatus.confirmed).build()));
    }

    /**
     * Chuyển đơn hàng sang đang xử lý (confirmed → processing).
     * @param id ID đơn hàng @return đơn hàng sau cập nhật
     */
    public OrderResult process(Long id) {
        Order o = load(id);
        o.getStatus().ensureCanTransitionTo(OrderStatus.processing);
        return OrderCommandMapper.toResult(orderRepo.save(o.toBuilder().status(OrderStatus.processing).build()));
    }

    /**
     * Hoàn tất đơn hàng (→ completed).
     * @param id ID đơn hàng @return đơn hàng sau cập nhật
     */
    public OrderResult complete(Long id) {
        Order o = load(id);
        o.getStatus().ensureCanTransitionTo(OrderStatus.completed);
        return OrderCommandMapper.toResult(orderRepo.save(o.toBuilder().status(OrderStatus.completed).build()));
    }

    /**
     * Hủy đơn hàng (→ cancelled).
     * @param id ID đơn hàng @return đơn hàng sau cập nhật
     */
    public OrderResult cancel(Long id) {
        Order o = load(id);
        o.getStatus().ensureCanTransitionTo(OrderStatus.cancelled);
        return OrderCommandMapper.toResult(orderRepo.save(o.toBuilder().status(OrderStatus.cancelled).build()));
    }

    /** Tải đơn hàng theo ID hoặc ném NotFoundException. */
    private Order load(Long id) {
        return orderRepo.findById(id).orElseThrow(() -> new NotFoundException("Order not found: " + id));
    }
}
