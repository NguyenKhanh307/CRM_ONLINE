package vn.com.be_crm.application.invoice.query;

import vn.com.be_crm.application.invoice.dto.InvoiceResult;
import vn.com.be_crm.application.invoice.mapper.InvoiceCommandMapper;
import vn.com.be_crm.core.page.PageRequest;
import vn.com.be_crm.core.page.PageResult;
import vn.com.be_crm.core.lookup.port.INameResolver;
import vn.com.be_crm.core.lookup.NameEnricher;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.core.util.LineItemTotals;
import vn.com.be_crm.domain.invoice.entity.InvoiceItem;
import vn.com.be_crm.domain.invoice.repository.IInvoiceItemRepository;
import vn.com.be_crm.domain.invoice.repository.IInvoiceRepository;

import java.util.List;
import java.util.stream.Collectors;

// lấy danh sách hóa đơn có phân trang, kèm tên khóa ngoại (đơn hàng, người phụ trách) + tổng
// tiền tính từ dòng hàng của từng bản ghi (không còn cột lưu sẵn)
public class ListInvoiceUseCase implements IUseCase<PageRequest, PageResult<InvoiceResult>> {
    private final IInvoiceRepository repo;
    private final IInvoiceItemRepository itemRepo;
    private final INameResolver names;

    public ListInvoiceUseCase(IInvoiceRepository repo, IInvoiceItemRepository itemRepo, INameResolver names) {
        this.repo = repo; this.itemRepo = itemRepo; this.names = names;
    }

    @Override public PageResult<InvoiceResult> execute(PageRequest r) {
        var page = repo.findAll(r);
        List<InvoiceResult> items = page.getItems().stream().map(InvoiceCommandMapper::toResult).collect(Collectors.toList());
        for (InvoiceResult res : items) {
            List<InvoiceItem> iItems = itemRepo.findAllByInvoiceId(res.getId());
            LineItemTotals.Totals t = LineItemTotals.compute(iItems,
                    InvoiceItem::getQuantity, InvoiceItem::getUnitPrice, InvoiceItem::getDiscount, InvoiceItem::getTaxRate);
            res.setSubtotal(t.subtotal()); res.setDiscount(t.discount());
            res.setTax(t.tax()); res.setTotal(t.total());
        }
        NameEnricher.apply(items, InvoiceResult::getOrderId, names::orderCodes, InvoiceResult::setOrderCode);
        NameEnricher.apply(items, InvoiceResult::getOwnerId, names::users, InvoiceResult::setOwnerName);
        NameEnricher.apply(items, InvoiceResult::getCreatedBy, names::users, InvoiceResult::setCreatedByName);
        NameEnricher.apply(items, InvoiceResult::getUpdatedBy, names::users, InvoiceResult::setUpdatedByName);
        return PageResult.<InvoiceResult>builder()
                .items(items)
                .total(page.getTotal()).page(page.getPage()).size(page.getSize()).build();
    }
}
