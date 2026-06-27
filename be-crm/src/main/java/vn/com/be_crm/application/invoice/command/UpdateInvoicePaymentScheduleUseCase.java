package vn.com.be_crm.application.invoice.command;

import vn.com.be_crm.application.invoice.dto.InvoicePaymentScheduleResult;
import vn.com.be_crm.application.invoice.dto.UpdateInvoicePaymentScheduleCommand;
import vn.com.be_crm.application.invoice.mapper.InvoicePaymentScheduleCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.invoice.entity.InvoicePaymentSchedule;
import vn.com.be_crm.domain.invoice.repository.IInvoicePaymentScheduleRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case cập nhật đợt thanh toán. */
public class UpdateInvoicePaymentScheduleUseCase implements IUseCase<UpdateInvoicePaymentScheduleCommand, InvoicePaymentScheduleResult> {
    private final IInvoicePaymentScheduleRepository repo;
    /** @param repo port lưu trữ */
    public UpdateInvoicePaymentScheduleUseCase(IInvoicePaymentScheduleRepository repo) { this.repo = repo; }
    /** Cập nhật InvoicePaymentSchedule. @param cmd @return InvoicePaymentScheduleResult @throws NotFoundException */
    @Override public InvoicePaymentScheduleResult execute(UpdateInvoicePaymentScheduleCommand cmd) {
        InvoicePaymentSchedule e = repo.findById(cmd.getId()).orElseThrow(() -> new NotFoundException("InvoicePaymentSchedule not found: " + cmd.getId()));
        return InvoicePaymentScheduleCommandMapper.toResult(repo.save(InvoicePaymentScheduleCommandMapper.toEntity(cmd, e)));
    }
}
