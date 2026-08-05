package vn.com.be_crm.application.order.query;

import vn.com.be_crm.application.order.dto.OrderResult;
import vn.com.be_crm.application.order.mapper.OrderCommandMapper;
import vn.com.be_crm.core.lookup.port.INameResolver;
import vn.com.be_crm.core.lookup.NameEnricher;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.core.util.LineItemTotals;
import vn.com.be_crm.domain.order.entity.OrderItem;
import vn.com.be_crm.domain.order.repository.IOrderItemRepository;
import vn.com.be_crm.domain.order.repository.IOrderRepository;
import vn.com.be_crm.core.error.frontend.NotFoundException;

import java.util.List;

// lấy đơn hàng theo ID — kèm tên khóa ngoại + tổng tiền tính từ dòng hàng (không còn cột lưu sẵn)
public class GetOrderUseCase implements IUseCase<Long, OrderResult> {
    private final IOrderRepository repo;
    private final IOrderItemRepository itemRepo;
    private final INameResolver names;

    public GetOrderUseCase(IOrderRepository repo, IOrderItemRepository itemRepo, INameResolver names) {
        this.repo = repo;
        this.itemRepo = itemRepo;
        this.names = names;
    }

    @Override public OrderResult execute(Long id) {
        var e = repo.findById(id).orElseThrow(() -> new NotFoundException("Order not found: " + id));
        OrderResult result = OrderCommandMapper.toResult(e);
        List<OrderItem> items = itemRepo.findAllByOrderId(id);
        LineItemTotals.Totals t = LineItemTotals.compute(items,
                OrderItem::getQuantity, OrderItem::getUnitPrice, OrderItem::getDiscount, OrderItem::getTaxRate);
        result.setSubtotal(t.subtotal()); result.setDiscount(t.discount());
        result.setTax(t.tax()); result.setTotal(t.total());

        List<OrderResult> one = List.of(result);
        NameEnricher.apply(one, OrderResult::getQuotationId, names::quotationCodes, OrderResult::setQuotationCode);
        NameEnricher.apply(one, OrderResult::getOwnerId, names::users, OrderResult::setOwnerName);
        NameEnricher.apply(one, OrderResult::getCreatedBy, names::users, OrderResult::setCreatedByName);
        NameEnricher.apply(one, OrderResult::getUpdatedBy, names::users, OrderResult::setUpdatedByName);
        return result;
    }
}
