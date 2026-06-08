package vn.com.be_crm.application.order.command;

import vn.com.be_crm.application.order.dto.CreateOrderPaymentScheduleCommand;
import vn.com.be_crm.application.order.dto.OrderPaymentScheduleResult;
import vn.com.be_crm.application.order.mapper.OrderPaymentScheduleCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.order.repository.IOrderPaymentScheduleRepository;

/** Use case tạo mới đợt thanh toán. */
public class CreateOrderPaymentScheduleUseCase implements IUseCase<CreateOrderPaymentScheduleCommand, OrderPaymentScheduleResult> {
    private final IOrderPaymentScheduleRepository repo;
    /** @param repo port lưu trữ */
    public CreateOrderPaymentScheduleUseCase(IOrderPaymentScheduleRepository repo) { this.repo = repo; }
    /** Tạo mới OrderPaymentSchedule. @param cmd @return OrderPaymentScheduleResult */
    @Override public OrderPaymentScheduleResult execute(CreateOrderPaymentScheduleCommand cmd) {
        return OrderPaymentScheduleCommandMapper.toResult(repo.save(OrderPaymentScheduleCommandMapper.toEntity(cmd)));
    }
}
