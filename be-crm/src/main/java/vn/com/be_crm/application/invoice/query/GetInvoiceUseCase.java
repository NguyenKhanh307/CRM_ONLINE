package vn.com.be_crm.application.invoice.query;

import vn.com.be_crm.application.invoice.dto.InvoiceResult;
import vn.com.be_crm.application.invoice.mapper.InvoiceCommandMapper;
import vn.com.be_crm.core.lookup.port.INameResolver;
import vn.com.be_crm.core.lookup.NameEnricher;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.core.util.LineItemTotals;
import vn.com.be_crm.domain.invoice.entity.InvoiceItem;
import vn.com.be_crm.domain.invoice.repository.IInvoiceItemRepository;
import vn.com.be_crm.domain.invoice.repository.IInvoiceRepository;
import vn.com.be_crm.core.error.frontend.NotFoundException;

import java.util.List;

// lấy hóa đơn theo ID — kèm tên khóa ngoại + tổng tiền tính từ dòng hàng (không còn cột lưu sẵn)
public class GetInvoiceUseCase implements IUseCase<Long, InvoiceResult> {
    private final IInvoiceRepository repo;
    private final IInvoiceItemRepository itemRepo;
    private final INameResolver names;

    public GetInvoiceUseCase(IInvoiceRepository repo, IInvoiceItemRepository itemRepo, INameResolver names) {
        this.repo = repo;
        this.itemRepo = itemRepo;
        this.names = names;
    }

    @Override public InvoiceResult execute(Long id) {
        var e = repo.findById(id).orElseThrow(() -> new NotFoundException("Invoice not found: " + id));
        InvoiceResult result = InvoiceCommandMapper.toResult(e);
        List<InvoiceItem> items = itemRepo.findAllByInvoiceId(id);
        LineItemTotals.Totals t = LineItemTotals.compute(items,
                InvoiceItem::getQuantity, InvoiceItem::getUnitPrice, InvoiceItem::getDiscount, InvoiceItem::getTaxRate);
        result.setSubtotal(t.subtotal()); result.setDiscount(t.discount());
        result.setTax(t.tax()); result.setTotal(t.total());

        List<InvoiceResult> one = List.of(result);
        NameEnricher.apply(one, InvoiceResult::getOrderId, names::orderCodes, InvoiceResult::setOrderCode);
        NameEnricher.apply(one, InvoiceResult::getOwnerId, names::users, InvoiceResult::setOwnerName);
        NameEnricher.apply(one, InvoiceResult::getCreatedBy, names::users, InvoiceResult::setCreatedByName);
        NameEnricher.apply(one, InvoiceResult::getUpdatedBy, names::users, InvoiceResult::setUpdatedByName);
        return result;
    }
}
