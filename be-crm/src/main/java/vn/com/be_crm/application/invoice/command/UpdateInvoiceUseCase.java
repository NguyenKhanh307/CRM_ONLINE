package vn.com.be_crm.application.invoice.command;

import vn.com.be_crm.core.util.CrossFieldRules;
import vn.com.be_crm.application.invoice.dto.InvoiceResult;
import vn.com.be_crm.application.invoice.dto.UpdateInvoiceCommand;
import vn.com.be_crm.application.invoice.mapper.InvoiceCommandMapper;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.core.util.LineItemTotals;
import vn.com.be_crm.domain.invoice.entity.Invoice;
import vn.com.be_crm.domain.invoice.entity.InvoiceItem;
import vn.com.be_crm.domain.invoice.repository.IInvoiceItemRepository;
import vn.com.be_crm.domain.invoice.repository.IInvoiceRepository;
import vn.com.be_crm.core.error.frontend.NotFoundException;

import java.util.List;

// cập nhật hóa đơn
public class UpdateInvoiceUseCase implements IUseCase<UpdateInvoiceCommand, InvoiceResult> {
    private final IInvoiceRepository repo;
    private final IInvoiceItemRepository itemRepo;

    public UpdateInvoiceUseCase(IInvoiceRepository repo, IInvoiceItemRepository itemRepo) {
        this.repo = repo;
        this.itemRepo = itemRepo;
    }

    @Override public InvoiceResult execute(UpdateInvoiceCommand cmd) {
        // ràng buộc khoảng thời gian: hạn thanh toán không được trước ngày hóa đơn
        CrossFieldRules.requireDateRange(cmd.getInvoiceDate(), cmd.getDueDate(), "Ngày hóa đơn", "Hạn thanh toán");
        Invoice e = repo.findById(cmd.getId()).orElseThrow(() -> new NotFoundException("Invoice not found: " + cmd.getId()));
        Invoice saved = repo.save(InvoiceCommandMapper.toEntity(cmd, e));

        InvoiceResult result = InvoiceCommandMapper.toResult(saved);
        List<InvoiceItem> items = itemRepo.findAllByInvoiceId(saved.getId());
        LineItemTotals.Totals t = LineItemTotals.compute(items,
                InvoiceItem::getQuantity, InvoiceItem::getUnitPrice, InvoiceItem::getDiscount, InvoiceItem::getTaxRate);
        result.setSubtotal(t.subtotal()); result.setDiscount(t.discount());
        result.setTax(t.tax()); result.setTotal(t.total());
        return result;
    }
}
