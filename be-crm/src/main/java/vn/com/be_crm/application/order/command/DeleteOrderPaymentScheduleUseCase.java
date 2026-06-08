package vn.com.be_crm.application.order.command;

import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.order.repository.IOrderPaymentScheduleRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case xóa đợt thanh toán. */
public class DeleteOrderPaymentScheduleUseCase implements IUseCase<Long, Void> {
    private final IOrderPaymentScheduleRepository repo;
    /** @param repo port lưu trữ */
    public DeleteOrderPaymentScheduleUseCase(IOrderPaymentScheduleRepository repo) { this.repo = repo; }
    /** Xóa OrderPaymentSchedule. @param id @return null @throws NotFoundException */
    @Override public Void execute(Long id) {
        repo.findById(id).orElseThrow(() -> new NotFoundException("OrderPaymentSchedule not found: " + id));
        repo.deleteById(id); return null;
    }
}
