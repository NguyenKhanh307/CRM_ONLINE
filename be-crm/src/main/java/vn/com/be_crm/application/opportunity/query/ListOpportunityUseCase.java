package vn.com.be_crm.application.opportunity.query;

import vn.com.be_crm.application.opportunity.dto.OpportunityResult;
import vn.com.be_crm.application.opportunity.mapper.OpportunityCommandMapper;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.application.shared.lookup.INameResolver;
import vn.com.be_crm.application.shared.lookup.NameEnricher;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityRepository;

import java.util.List;
import java.util.stream.Collectors;

/** Use case lấy danh sách cơ hội bán hàng có phân trang. */
public class ListOpportunityUseCase implements IUseCase<PageRequest, PageResult<OpportunityResult>> {
    private final IOpportunityRepository repo;
    private final INameResolver names;
    /** @param repo port lưu trữ @param names port tra tên khóa ngoại */
    public ListOpportunityUseCase(IOpportunityRepository repo, INameResolver names) { this.repo = repo; this.names = names; }

    /**
     * Lấy danh sách Opportunity có phân trang, kèm tên khóa ngoại (khách hàng, liên hệ, giai đoạn, người phụ trách).
     * @param r tham số phân trang @return PageResult
     */
    @Override
    public PageResult<OpportunityResult> execute(PageRequest r) {
        var page = repo.findAll(r);
        List<OpportunityResult> items = page.getItems().stream().map(OpportunityCommandMapper::toResult).collect(Collectors.toList());
        NameEnricher.apply(items, OpportunityResult::getCustomerId, names::customers, OpportunityResult::setCustomerName);
        NameEnricher.apply(items, OpportunityResult::getContactId, names::contacts, OpportunityResult::setContactName);
        NameEnricher.apply(items, OpportunityResult::getStageId, names::stages, OpportunityResult::setStageName);
        NameEnricher.apply(items, OpportunityResult::getCampaignId, names::campaigns, OpportunityResult::setCampaignName);
        NameEnricher.apply(items, OpportunityResult::getOwnerId, names::users, OpportunityResult::setOwnerName);
        NameEnricher.apply(items, OpportunityResult::getCreatedBy, names::users, OpportunityResult::setCreatedByName);
        NameEnricher.apply(items, OpportunityResult::getUpdatedBy, names::users, OpportunityResult::setUpdatedByName);
        return PageResult.<OpportunityResult>builder()
                .items(items)
                .total(page.getTotal()).page(page.getPage()).size(page.getSize()).build();
    }
}
