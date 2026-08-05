package vn.com.be_crm.application.order.query;

import vn.com.be_crm.application.order.dto.OrderResult;
import vn.com.be_crm.application.order.mapper.OrderCommandMapper;
import vn.com.be_crm.core.page.PageRequest;
import vn.com.be_crm.core.page.PageResult;
import vn.com.be_crm.core.lookup.port.INameResolver;
import vn.com.be_crm.core.lookup.NameEnricher;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.core.util.LineItemTotals;
import vn.com.be_crm.domain.order.entity.OrderItem;
import vn.com.be_crm.domain.order.repository.IOrderItemRepository;
import vn.com.be_crm.domain.order.repository.IOrderRepository;

import java.util.List;
import java.util.stream.Collectors;

// lấy danh sách đơn hàng có phân trang, kèm tên khóa ngoại (báo giá, người phụ trách) + tổng
// tiền tính từ dòng hàng của từng bản ghi (không còn cột lưu sẵn)
public class ListOrderUseCase implements IUseCase<PageRequest, PageResult<OrderResult>> {
    private final IOrderRepository repo;
    private final IOrderItemRepository itemRepo;
    private final INameResolver names;

    public ListOrderUseCase(IOrderRepository repo, IOrderItemRepository itemRepo, INameResolver names) {
        this.repo = repo; this.itemRepo = itemRepo; this.names = names;
    }

    @Override public PageResult<OrderResult> execute(PageRequest r) {
        var page = repo.findAll(r);
        List<OrderResult> items = page.getItems().stream().map(OrderCommandMapper::toResult).collect(Collectors.toList());
        for (OrderResult res : items) {
            List<OrderItem> oItems = itemRepo.findAllByOrderId(res.getId());
            LineItemTotals.Totals t = LineItemTotals.compute(oItems,
                    OrderItem::getQuantity, OrderItem::getUnitPrice, OrderItem::getDiscount, OrderItem::getTaxRate);
            res.setSubtotal(t.subtotal()); res.setDiscount(t.discount());
            res.setTax(t.tax()); res.setTotal(t.total());
        }
        NameEnricher.apply(items, OrderResult::getQuotationId, names::quotationCodes, OrderResult::setQuotationCode);
        NameEnricher.apply(items, OrderResult::getOwnerId, names::users, OrderResult::setOwnerName);
        NameEnricher.apply(items, OrderResult::getCreatedBy, names::users, OrderResult::setCreatedByName);
        NameEnricher.apply(items, OrderResult::getUpdatedBy, names::users, OrderResult::setUpdatedByName);
        return PageResult.<OrderResult>builder()
                .items(items)
                .total(page.getTotal()).page(page.getPage()).size(page.getSize()).build();
    }
}
