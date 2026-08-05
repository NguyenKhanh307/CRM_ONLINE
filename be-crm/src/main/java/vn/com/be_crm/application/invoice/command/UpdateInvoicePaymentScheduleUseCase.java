package vn.com.be_crm.application.invoice.command;

import vn.com.be_crm.application.invoice.dto.InvoicePaymentScheduleResult;
import vn.com.be_crm.application.invoice.dto.UpdateInvoicePaymentScheduleCommand;
import vn.com.be_crm.application.invoice.mapper.InvoicePaymentScheduleCommandMapper;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.core.util.LineItemTotals;
import vn.com.be_crm.domain.invoice.entity.InvoiceItem;
import vn.com.be_crm.domain.invoice.entity.InvoicePaymentSchedule;
import vn.com.be_crm.domain.invoice.repository.IInvoiceItemRepository;
import vn.com.be_crm.domain.invoice.repository.IInvoicePaymentScheduleRepository;
import vn.com.be_crm.domain.invoice.repository.IInvoiceRepository;
import vn.com.be_crm.core.error.frontend.DomainException;
import vn.com.be_crm.core.error.frontend.NotFoundException;

import java.math.BigDecimal;

// cập nhật đợt thanh toán
public class UpdateInvoicePaymentScheduleUseCase implements IUseCase<UpdateInvoicePaymentScheduleCommand, InvoicePaymentScheduleResult> {
    private final IInvoicePaymentScheduleRepository repo;
    private final IInvoiceRepository invoiceRepo;
    private final IInvoiceItemRepository invoiceItemRepo;

    public UpdateInvoicePaymentScheduleUseCase(IInvoicePaymentScheduleRepository repo, IInvoiceRepository invoiceRepo,
                                                IInvoiceItemRepository invoiceItemRepo) {
        this.repo = repo;
        this.invoiceRepo = invoiceRepo;
        this.invoiceItemRepo = invoiceItemRepo;
    }

    @Override public InvoicePaymentScheduleResult execute(UpdateInvoicePaymentScheduleCommand cmd) {
        InvoicePaymentSchedule e = repo.findById(cmd.getId()).orElseThrow(() -> new NotFoundException("InvoicePaymentSchedule not found: " + cmd.getId()));
        InvoicePaymentSchedule merged = InvoicePaymentScheduleCommandMapper.toEntity(cmd, e);

        invoiceRepo.findById(e.getInvoiceId())
                .orElseThrow(() -> new NotFoundException("Invoice not found: " + e.getInvoiceId()));
        BigDecimal otherPaid = repo.findAllByInvoiceId(e.getInvoiceId()).stream()
                .filter(s -> !s.getId().equals(cmd.getId()))
                .map(InvoicePaymentSchedule::getPaidAmount).filter(a -> a != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        java.util.List<InvoiceItem> items = invoiceItemRepo.findAllByInvoiceId(e.getInvoiceId());
        BigDecimal total = LineItemTotals.compute(items,
                InvoiceItem::getQuantity, InvoiceItem::getUnitPrice, InvoiceItem::getDiscount, InvoiceItem::getTaxRate).total();
        if (otherPaid.add(merged.getPaidAmount()).compareTo(total) > 0) {
            throw new DomainException("Số tiền thu vượt quá tổng hóa đơn");
        }
        return InvoicePaymentScheduleCommandMapper.toResult(repo.save(merged));
    }
}
