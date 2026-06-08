package vn.com.be_crm.application.order.query;

import vn.com.be_crm.application.order.dto.OrderDeliveryTrackingResult;
import vn.com.be_crm.application.order.mapper.OrderDeliveryTrackingCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.order.repository.IOrderDeliveryTrackingRepository;

import java.util.List;
import java.util.stream.Collectors;

/** Use case lấy danh sách theo dõi giao hàng theo orderId. */
public class ListOrderDeliveryTrackingUseCase implements IUseCase<Long, List<OrderDeliveryTrackingResult>> {
    private final IOrderDeliveryTrackingRepository repo;
    /** @param repo port lưu trữ */
    public ListOrderDeliveryTrackingUseCase(IOrderDeliveryTrackingRepository repo) { this.repo = repo; }
    /** Lấy danh sách OrderDeliveryTracking theo orderId. @param orderId @return danh sách */
    @Override public List<OrderDeliveryTrackingResult> execute(Long orderId) {
        return repo.findAllByOrderId(orderId).stream().map(OrderDeliveryTrackingCommandMapper::toResult).collect(Collectors.toList());
    }
}
