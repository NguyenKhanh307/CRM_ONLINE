package vn.com.be_crm.application.order.command;

import vn.com.be_crm.application.order.dto.CreateOrderDeliveryTrackingCommand;
import vn.com.be_crm.application.order.dto.OrderDeliveryTrackingResult;
import vn.com.be_crm.application.order.mapper.OrderDeliveryTrackingCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.order.repository.IOrderDeliveryTrackingRepository;

/** Use case tạo mới theo dõi giao hàng. */
public class CreateOrderDeliveryTrackingUseCase implements IUseCase<CreateOrderDeliveryTrackingCommand, OrderDeliveryTrackingResult> {
    private final IOrderDeliveryTrackingRepository repo;
    /** @param repo port lưu trữ */
    public CreateOrderDeliveryTrackingUseCase(IOrderDeliveryTrackingRepository repo) { this.repo = repo; }
    /** Tạo mới OrderDeliveryTracking. @param cmd @return OrderDeliveryTrackingResult */
    @Override public OrderDeliveryTrackingResult execute(CreateOrderDeliveryTrackingCommand cmd) {
        return OrderDeliveryTrackingCommandMapper.toResult(repo.save(OrderDeliveryTrackingCommandMapper.toEntity(cmd)));
    }
}
