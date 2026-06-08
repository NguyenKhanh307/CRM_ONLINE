package vn.com.be_crm.application.order.command;

import vn.com.be_crm.application.order.dto.OrderDeliveryTrackingResult;
import vn.com.be_crm.application.order.dto.UpdateOrderDeliveryTrackingCommand;
import vn.com.be_crm.application.order.mapper.OrderDeliveryTrackingCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.order.entity.OrderDeliveryTracking;
import vn.com.be_crm.domain.order.repository.IOrderDeliveryTrackingRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case cập nhật theo dõi giao hàng. */
public class UpdateOrderDeliveryTrackingUseCase implements IUseCase<UpdateOrderDeliveryTrackingCommand, OrderDeliveryTrackingResult> {
    private final IOrderDeliveryTrackingRepository repo;
    /** @param repo port lưu trữ */
    public UpdateOrderDeliveryTrackingUseCase(IOrderDeliveryTrackingRepository repo) { this.repo = repo; }
    /** Cập nhật OrderDeliveryTracking. @param cmd @return OrderDeliveryTrackingResult @throws NotFoundException */
    @Override public OrderDeliveryTrackingResult execute(UpdateOrderDeliveryTrackingCommand cmd) {
        OrderDeliveryTracking e = repo.findById(cmd.getId()).orElseThrow(() -> new NotFoundException("OrderDeliveryTracking not found: " + cmd.getId()));
        return OrderDeliveryTrackingCommandMapper.toResult(repo.save(OrderDeliveryTrackingCommandMapper.toEntity(cmd, e)));
    }
}
