package vn.com.be_crm.application.invoice.command;

import vn.com.be_crm.application.invoice.dto.CreateInvoicePaymentScheduleCommand;
import vn.com.be_crm.application.invoice.dto.InvoicePaymentScheduleResult;
import vn.com.be_crm.application.invoice.mapper.InvoicePaymentScheduleCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.invoice.entity.Invoice;
import vn.com.be_crm.domain.invoice.entity.InvoicePaymentSchedule;
import vn.com.be_crm.domain.invoice.repository.IInvoicePaymentScheduleRepository;
import vn.com.be_crm.domain.invoice.repository.IInvoiceRepository;
import vn.com.be_crm.domain.shared.exception.DomainException;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

import java.math.BigDecimal;

/** Use case tạo mới đợt thanh toán. */
public class CreateInvoicePaymentScheduleUseCase implements IUseCase<CreateInvoicePaymentScheduleCommand, InvoicePaymentScheduleResult> {
    private final IInvoicePaymentScheduleRepository repo;
    private final IInvoiceRepository invoiceRepo;
    /** @param repo port lưu trữ đợt thanh toán @param invoiceRepo port lưu trữ hóa đơn — kiểm tổng thu vượt quá */
    public CreateInvoicePaymentScheduleUseCase(IInvoicePaymentScheduleRepository repo, IInvoiceRepository invoiceRepo) {
        this.repo = repo;
        this.invoiceRepo = invoiceRepo;
    }
    /** Tạo mới InvoicePaymentSchedule. @param cmd @return InvoicePaymentScheduleResult */
    @Override public InvoicePaymentScheduleResult execute(CreateInvoicePaymentScheduleCommand cmd) {
        InvoicePaymentSchedule entity = InvoicePaymentScheduleCommandMapper.toEntity(cmd);
        Invoice invoice = invoiceRepo.findById(cmd.getInvoiceId())
                .orElseThrow(() -> new NotFoundException("Invoice not found: " + cmd.getInvoiceId()));
        BigDecimal otherPaid = repo.findAllByInvoiceId(cmd.getInvoiceId()).stream()
                .map(InvoicePaymentSchedule::getPaidAmount).filter(a -> a != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal total = invoice.getTotal() != null ? invoice.getTotal() : BigDecimal.ZERO;
        if (otherPaid.add(entity.getPaidAmount()).compareTo(total) > 0) {
            throw new DomainException("Số tiền thu vượt quá tổng hóa đơn");
        }
        return InvoicePaymentScheduleCommandMapper.toResult(repo.save(entity));
    }
}
