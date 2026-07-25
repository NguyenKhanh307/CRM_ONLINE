package vn.com.be_crm.application.order.command;

import vn.com.be_crm.application.shared.util.CrossFieldRules;
import vn.com.be_crm.application.order.dto.OrderResult;
import vn.com.be_crm.application.order.dto.UpdateOrderCommand;
import vn.com.be_crm.application.order.mapper.OrderCommandMapper;
import vn.com.be_crm.application.shared.notify.NotifyAssignmentUseCase;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.order.entity.Order;
import vn.com.be_crm.domain.order.repository.IOrderRepository;
import vn.com.be_crm.domain.shared.exception.DomainException;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

import java.util.Objects;

/** Use case cập nhật đơn hàng. */
public class UpdateOrderUseCase implements IUseCase<UpdateOrderCommand, OrderResult> {
    private final IOrderRepository repo;
    private final RecomputeOrderTotalsUseCase recomputeUC;
    private final NotifyAssignmentUseCase notifyUC;

    /** @param repo port lưu trữ @param recomputeUC tính lại tổng tiền từ dòng hàng @param notifyUC báo cho người phụ trách mới */
    public UpdateOrderUseCase(IOrderRepository repo, RecomputeOrderTotalsUseCase recomputeUC,
                              NotifyAssignmentUseCase notifyUC) {
        this.repo = repo;
        this.recomputeUC = recomputeUC;
        this.notifyUC = notifyUC;
    }

    /**
     * Cập nhật Order; chặn sửa khi đã khóa (đã xuất hóa đơn).
     * Tổng tiền KHÔNG lấy từ client mà tính lại từ dòng hàng sau khi lưu.
     * @param cmd @return OrderResult @throws NotFoundException
     */
    @Override public OrderResult execute(UpdateOrderCommand cmd) {
        // Ràng buộc khoảng thời gian: ngày giao hàng không được trước ngày đặt hàng
        CrossFieldRules.requireDateRange(cmd.getOrderDate(), cmd.getDeliveryDate(), "Ngày đặt hàng", "Ngày giao hàng");
        Order e = repo.findById(cmd.getId()).orElseThrow(() -> new NotFoundException("Order not found: " + cmd.getId()));
        if (e.isLocked()) throw new DomainException("Đơn hàng đã khóa (đã xuất hóa đơn), không thể sửa");
        repo.save(OrderCommandMapper.toEntity(cmd, e));
        recomputeUC.execute(cmd.getId());
        Order saved = repo.findById(cmd.getId())
                .orElseThrow(() -> new NotFoundException("Order not found: " + cmd.getId()));
        // Đổi người phụ trách → báo cho người nhận việc
        if (!Objects.equals(e.getOwnerId(), saved.getOwnerId())) {
            notifyUC.notifyAssigned("order", "đơn hàng", saved.getOwnerId(), saved.getId(), saved.getCode());
        }
        return OrderCommandMapper.toResult(saved);
    }
}
