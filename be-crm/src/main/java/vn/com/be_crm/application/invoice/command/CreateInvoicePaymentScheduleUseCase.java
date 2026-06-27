package vn.com.be_crm.application.invoice.command;

import vn.com.be_crm.application.invoice.dto.CreateInvoicePaymentScheduleCommand;
import vn.com.be_crm.application.invoice.dto.InvoicePaymentScheduleResult;
import vn.com.be_crm.application.invoice.mapper.InvoicePaymentScheduleCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.invoice.repository.IInvoicePaymentScheduleRepository;

/** Use case tạo mới đợt thanh toán. */
public class CreateInvoicePaymentScheduleUseCase implements IUseCase<CreateInvoicePaymentScheduleCommand, InvoicePaymentScheduleResult> {
    private final IInvoicePaymentScheduleRepository repo;
    /** @param repo port lưu trữ */
    public CreateInvoicePaymentScheduleUseCase(IInvoicePaymentScheduleRepository repo) { this.repo = repo; }
    /** Tạo mới InvoicePaymentSchedule. @param cmd @return InvoicePaymentScheduleResult */
    @Override public InvoicePaymentScheduleResult execute(CreateInvoicePaymentScheduleCommand cmd) {
        return InvoicePaymentScheduleCommandMapper.toResult(repo.save(InvoicePaymentScheduleCommandMapper.toEntity(cmd)));
    }
}
