package vn.com.be_crm.application.lead.query;

import vn.com.be_crm.application.lead.dto.LeadResult;
import vn.com.be_crm.application.lead.mapper.LeadCommandMapper;
import vn.com.be_crm.core.page.PageRequest;
import vn.com.be_crm.core.page.PageResult;
import vn.com.be_crm.core.lookup.port.INameResolver;
import vn.com.be_crm.core.lookup.NameEnricher;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.lead.repository.ILeadRepository;

import java.util.List;
import java.util.stream.Collectors;

// lấy danh sách tiềm năng có phân trang, kèm tên khóa ngoại (owner, khách hàng, liên hệ, chiến dịch)
public class ListLeadUseCase implements IUseCase<PageRequest, PageResult<LeadResult>> {
    private final ILeadRepository repo;
    private final INameResolver names;

    public ListLeadUseCase(ILeadRepository repo, INameResolver names) { this.repo = repo; this.names = names; }

    @Override
    public PageResult<LeadResult> execute(PageRequest r) {
        var page = repo.findAll(r);
        List<LeadResult> items = page.getItems().stream().map(LeadCommandMapper::toResult).collect(Collectors.toList());
        NameEnricher.apply(items, LeadResult::getOwnerId, names::users, LeadResult::setOwnerName);
        NameEnricher.apply(items, LeadResult::getCustomerId, names::customers, LeadResult::setCustomerName);
        NameEnricher.apply(items, LeadResult::getContactId, names::contacts, LeadResult::setContactName);
        NameEnricher.apply(items, LeadResult::getCampaignId, names::campaigns, LeadResult::setCampaignName);
        NameEnricher.apply(items, LeadResult::getCreatedBy, names::users, LeadResult::setCreatedByName);
        NameEnricher.apply(items, LeadResult::getUpdatedBy, names::users, LeadResult::setUpdatedByName);
        return PageResult.<LeadResult>builder()
                .items(items)
                .total(page.getTotal()).page(page.getPage()).size(page.getSize()).build();
    }
}
