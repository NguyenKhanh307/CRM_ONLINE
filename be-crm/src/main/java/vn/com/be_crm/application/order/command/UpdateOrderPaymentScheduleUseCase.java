package vn.com.be_crm.application.order.command;

import vn.com.be_crm.application.order.dto.OrderPaymentScheduleResult;
import vn.com.be_crm.application.order.dto.UpdateOrderPaymentScheduleCommand;
import vn.com.be_crm.application.order.mapper.OrderPaymentScheduleCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.order.entity.OrderPaymentSchedule;
import vn.com.be_crm.domain.order.repository.IOrderPaymentScheduleRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case cập nhật đợt thanh toán. */
public class UpdateOrderPaymentScheduleUseCase implements IUseCase<UpdateOrderPaymentScheduleCommand, OrderPaymentScheduleResult> {
    private final IOrderPaymentScheduleRepository repo;
    /** @param repo port lưu trữ */
    public UpdateOrderPaymentScheduleUseCase(IOrderPaymentScheduleRepository repo) { this.repo = repo; }
    /** Cập nhật OrderPaymentSchedule. @param cmd @return OrderPaymentScheduleResult @throws NotFoundException */
    @Override public OrderPaymentScheduleResult execute(UpdateOrderPaymentScheduleCommand cmd) {
        OrderPaymentSchedule e = repo.findById(cmd.getId()).orElseThrow(() -> new NotFoundException("OrderPaymentSchedule not found: " + cmd.getId()));
        return OrderPaymentScheduleCommandMapper.toResult(repo.save(OrderPaymentScheduleCommandMapper.toEntity(cmd, e)));
    }
}
