package vn.com.be_crm.application.invoice.command;

import vn.com.be_crm.application.invoice.dto.CreateInvoicePaymentScheduleCommand;
import vn.com.be_crm.application.invoice.dto.InvoicePaymentScheduleResult;
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

// tạo mới đợt thanh toán
public class CreateInvoicePaymentScheduleUseCase implements IUseCase<CreateInvoicePaymentScheduleCommand, InvoicePaymentScheduleResult> {
    private final IInvoicePaymentScheduleRepository repo;
    private final IInvoiceRepository invoiceRepo;
    private final IInvoiceItemRepository invoiceItemRepo;

    public CreateInvoicePaymentScheduleUseCase(IInvoicePaymentScheduleRepository repo, IInvoiceRepository invoiceRepo,
                                                IInvoiceItemRepository invoiceItemRepo) {
        this.repo = repo;
        this.invoiceRepo = invoiceRepo;
        this.invoiceItemRepo = invoiceItemRepo;
    }

    @Override public InvoicePaymentScheduleResult execute(CreateInvoicePaymentScheduleCommand cmd) {
        InvoicePaymentSchedule entity = InvoicePaymentScheduleCommandMapper.toEntity(cmd);
        invoiceRepo.findById(cmd.getInvoiceId())
                .orElseThrow(() -> new NotFoundException("Invoice not found: " + cmd.getInvoiceId()));
        BigDecimal otherPaid = repo.findAllByInvoiceId(cmd.getInvoiceId()).stream()
                .map(InvoicePaymentSchedule::getPaidAmount).filter(a -> a != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        // tổng hóa đơn tính từ dòng hàng (không còn cột lưu sẵn)
        java.util.List<InvoiceItem> items = invoiceItemRepo.findAllByInvoiceId(cmd.getInvoiceId());
        BigDecimal total = LineItemTotals.compute(items,
                InvoiceItem::getQuantity, InvoiceItem::getUnitPrice, InvoiceItem::getDiscount, InvoiceItem::getTaxRate).total();
        if (otherPaid.add(entity.getPaidAmount()).compareTo(total) > 0) {
            throw new DomainException("Số tiền thu vượt quá tổng hóa đơn");
        }
        return InvoicePaymentScheduleCommandMapper.toResult(repo.save(entity));
    }
}
