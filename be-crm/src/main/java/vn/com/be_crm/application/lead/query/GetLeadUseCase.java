package vn.com.be_crm.application.lead.query;

import vn.com.be_crm.application.lead.dto.LeadResult;
import vn.com.be_crm.application.lead.mapper.LeadCommandMapper;
import vn.com.be_crm.application.shared.lookup.INameResolver;
import vn.com.be_crm.application.shared.lookup.NameEnricher;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.lead.repository.ILeadRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

import java.util.List;

/** Use case lấy tiềm năng theo ID — kèm tên khóa ngoại để trang chi tiết hiển thị trực tiếp. */
public class GetLeadUseCase implements IUseCase<Long, LeadResult> {
    private final ILeadRepository repo;
    private final INameResolver names;

    /** @param repo port lưu trữ @param names port tra tên khóa ngoại */
    public GetLeadUseCase(ILeadRepository repo, INameResolver names) {
        this.repo = repo;
        this.names = names;
    }

    /**
     * Lấy Lead theo ID.
     * @param id ID @return LeadResult (đã điền tên khóa ngoại)
     * @throws NotFoundException nếu không tìm thấy
     */
    @Override
    public LeadResult execute(Long id) {
        LeadResult result = LeadCommandMapper.toResult(
                repo.findById(id).orElseThrow(() -> new NotFoundException("Lead not found: " + id)));
        List<LeadResult> one = List.of(result);
        NameEnricher.apply(one, LeadResult::getOwnerId, names::users, LeadResult::setOwnerName);
        NameEnricher.apply(one, LeadResult::getCustomerId, names::customers, LeadResult::setCustomerName);
        NameEnricher.apply(one, LeadResult::getContactId, names::contacts, LeadResult::setContactName);
        NameEnricher.apply(one, LeadResult::getCampaignId, names::campaigns, LeadResult::setCampaignName);
        NameEnricher.apply(one, LeadResult::getCreatedBy, names::users, LeadResult::setCreatedByName);
        NameEnricher.apply(one, LeadResult::getUpdatedBy, names::users, LeadResult::setUpdatedByName);
        return result;
    }
}
