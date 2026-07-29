package vn.com.be_crm.application.invoice.command;

import vn.com.be_crm.application.invoice.dto.InvoicePaymentScheduleResult;
import vn.com.be_crm.application.invoice.dto.UpdateInvoicePaymentScheduleCommand;
import vn.com.be_crm.application.invoice.mapper.InvoicePaymentScheduleCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.invoice.entity.Invoice;
import vn.com.be_crm.domain.invoice.entity.InvoicePaymentSchedule;
import vn.com.be_crm.domain.invoice.repository.IInvoicePaymentScheduleRepository;
import vn.com.be_crm.domain.invoice.repository.IInvoiceRepository;
import vn.com.be_crm.domain.shared.exception.DomainException;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

import java.math.BigDecimal;

/** Use case cập nhật đợt thanh toán. */
public class UpdateInvoicePaymentScheduleUseCase implements IUseCase<UpdateInvoicePaymentScheduleCommand, InvoicePaymentScheduleResult> {
    private final IInvoicePaymentScheduleRepository repo;
    private final IInvoiceRepository invoiceRepo;
    /** @param repo port lưu trữ đợt thanh toán @param invoiceRepo port lưu trữ hóa đơn — kiểm tổng thu vượt quá */
    public UpdateInvoicePaymentScheduleUseCase(IInvoicePaymentScheduleRepository repo, IInvoiceRepository invoiceRepo) {
        this.repo = repo;
        this.invoiceRepo = invoiceRepo;
    }
    /** Cập nhật InvoicePaymentSchedule. @param cmd @return InvoicePaymentScheduleResult @throws NotFoundException */
    @Override public InvoicePaymentScheduleResult execute(UpdateInvoicePaymentScheduleCommand cmd) {
        InvoicePaymentSchedule e = repo.findById(cmd.getId()).orElseThrow(() -> new NotFoundException("InvoicePaymentSchedule not found: " + cmd.getId()));
        InvoicePaymentSchedule merged = InvoicePaymentScheduleCommandMapper.toEntity(cmd, e);

        Invoice invoice = invoiceRepo.findById(e.getInvoiceId())
                .orElseThrow(() -> new NotFoundException("Invoice not found: " + e.getInvoiceId()));
        BigDecimal otherPaid = repo.findAllByInvoiceId(e.getInvoiceId()).stream()
                .filter(s -> !s.getId().equals(cmd.getId()))
                .map(InvoicePaymentSchedule::getPaidAmount).filter(a -> a != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal total = invoice.getTotal() != null ? invoice.getTotal() : BigDecimal.ZERO;
        if (otherPaid.add(merged.getPaidAmount()).compareTo(total) > 0) {
            throw new DomainException("Số tiền thu vượt quá tổng hóa đơn");
        }
        return InvoicePaymentScheduleCommandMapper.toResult(repo.save(merged));
    }
}
