package vn.com.be_crm.application.order.query;

import vn.com.be_crm.application.order.dto.OrderResult;
import vn.com.be_crm.application.order.mapper.OrderCommandMapper;
import vn.com.be_crm.core.lookup.port.INameResolver;
import vn.com.be_crm.core.lookup.NameEnricher;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.order.repository.IOrderRepository;
import vn.com.be_crm.core.error.frontend.NotFoundException;

import java.util.List;

/** Use case lấy đơn hàng theo ID — kèm tên khóa ngoại để trang chi tiết hiển thị trực tiếp. */
public class GetOrderUseCase implements IUseCase<Long, OrderResult> {
    private final IOrderRepository repo;
    private final INameResolver names;

    /** @param repo port lưu trữ @param names port tra tên khóa ngoại */
    public GetOrderUseCase(IOrderRepository repo, INameResolver names) {
        this.repo = repo;
        this.names = names;
    }

    /** Lấy Order theo ID. @param id @return OrderResult (đã điền tên khóa ngoại) @throws NotFoundException */
    @Override public OrderResult execute(Long id) {
        OrderResult result = OrderCommandMapper.toResult(
                repo.findById(id).orElseThrow(() -> new NotFoundException("Order not found: " + id)));
        List<OrderResult> one = List.of(result);
        NameEnricher.apply(one, OrderResult::getCustomerId, names::customers, OrderResult::setCustomerName);
        NameEnricher.apply(one, OrderResult::getContactId, names::contacts, OrderResult::setContactName);
        NameEnricher.apply(one, OrderResult::getQuotationId, names::quotationCodes, OrderResult::setQuotationCode);
        NameEnricher.apply(one, OrderResult::getOpportunityId, names::opportunities, OrderResult::setOpportunityName);
        NameEnricher.apply(one, OrderResult::getCampaignId, names::campaigns, OrderResult::setCampaignName);
        NameEnricher.apply(one, OrderResult::getOwnerId, names::users, OrderResult::setOwnerName);
        NameEnricher.apply(one, OrderResult::getCreatedBy, names::users, OrderResult::setCreatedByName);
        NameEnricher.apply(one, OrderResult::getUpdatedBy, names::users, OrderResult::setUpdatedByName);
        return result;
    }
}
