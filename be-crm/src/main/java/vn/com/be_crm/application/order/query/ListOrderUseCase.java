package vn.com.be_crm.application.order.query;

import vn.com.be_crm.application.order.dto.OrderResult;
import vn.com.be_crm.application.order.mapper.OrderCommandMapper;
import vn.com.be_crm.core.page.PageRequest;
import vn.com.be_crm.core.page.PageResult;
import vn.com.be_crm.core.lookup.port.INameResolver;
import vn.com.be_crm.core.lookup.NameEnricher;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.order.repository.IOrderRepository;

import java.util.List;
import java.util.stream.Collectors;

/** Use case lấy danh sách đơn hàng có phân trang. */
public class ListOrderUseCase implements IUseCase<PageRequest, PageResult<OrderResult>> {
    private final IOrderRepository repo;
    private final INameResolver names;
    /** @param repo port lưu trữ @param names port tra tên khóa ngoại */
    public ListOrderUseCase(IOrderRepository repo, INameResolver names) { this.repo = repo; this.names = names; }
    /** Lấy danh sách Order kèm tên khóa ngoại (khách hàng, liên hệ, báo giá, cơ hội, chiến dịch, người phụ trách). @param r phân trang @return PageResult */
    @Override public PageResult<OrderResult> execute(PageRequest r) {
        var page = repo.findAll(r);
        List<OrderResult> items = page.getItems().stream().map(OrderCommandMapper::toResult).collect(Collectors.toList());
        NameEnricher.apply(items, OrderResult::getCustomerId, names::customers, OrderResult::setCustomerName);
        NameEnricher.apply(items, OrderResult::getContactId, names::contacts, OrderResult::setContactName);
        NameEnricher.apply(items, OrderResult::getQuotationId, names::quotationCodes, OrderResult::setQuotationCode);
        NameEnricher.apply(items, OrderResult::getOpportunityId, names::opportunities, OrderResult::setOpportunityName);
        NameEnricher.apply(items, OrderResult::getCampaignId, names::campaigns, OrderResult::setCampaignName);
        NameEnricher.apply(items, OrderResult::getOwnerId, names::users, OrderResult::setOwnerName);
        NameEnricher.apply(items, OrderResult::getCreatedBy, names::users, OrderResult::setCreatedByName);
        NameEnricher.apply(items, OrderResult::getUpdatedBy, names::users, OrderResult::setUpdatedByName);
        return PageResult.<OrderResult>builder()
                .items(items)
                .total(page.getTotal()).page(page.getPage()).size(page.getSize()).build();
    }
}
