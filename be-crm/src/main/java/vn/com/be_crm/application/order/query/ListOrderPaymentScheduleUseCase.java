package vn.com.be_crm.application.order.query;

import vn.com.be_crm.application.order.dto.OrderPaymentScheduleResult;
import vn.com.be_crm.application.order.mapper.OrderPaymentScheduleCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.order.repository.IOrderPaymentScheduleRepository;

import java.util.List;
import java.util.stream.Collectors;

/** Use case lấy danh sách đợt thanh toán theo orderId. */
public class ListOrderPaymentScheduleUseCase implements IUseCase<Long, List<OrderPaymentScheduleResult>> {
    private final IOrderPaymentScheduleRepository repo;
    /** @param repo port lưu trữ */
    public ListOrderPaymentScheduleUseCase(IOrderPaymentScheduleRepository repo) { this.repo = repo; }
    /** Lấy danh sách OrderPaymentSchedule theo orderId. @param orderId @return danh sách */
    @Override public List<OrderPaymentScheduleResult> execute(Long orderId) {
        return repo.findAllByOrderId(orderId).stream().map(OrderPaymentScheduleCommandMapper::toResult).collect(Collectors.toList());
    }
}
