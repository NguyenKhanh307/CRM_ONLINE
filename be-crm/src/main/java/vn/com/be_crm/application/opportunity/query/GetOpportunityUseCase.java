package vn.com.be_crm.application.opportunity.query;

import vn.com.be_crm.application.opportunity.dto.OpportunityResult;
import vn.com.be_crm.application.opportunity.mapper.OpportunityCommandMapper;
import vn.com.be_crm.application.shared.lookup.INameResolver;
import vn.com.be_crm.application.shared.lookup.NameEnricher;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

import java.util.List;

/** Use case lấy cơ hội bán hàng theo ID — kèm tên khóa ngoại cho trang chi tiết 360°. */
public class GetOpportunityUseCase implements IUseCase<Long, OpportunityResult> {
    private final IOpportunityRepository repo;
    private final INameResolver names;

    /** @param repo port lưu trữ @param names port tra tên khóa ngoại */
    public GetOpportunityUseCase(IOpportunityRepository repo, INameResolver names) {
        this.repo = repo;
        this.names = names;
    }

    /**
     * Lấy Opportunity theo ID.
     * @param id ID @return OpportunityResult (đã điền customerName/contactName/ownerName/stageName)
     * @throws NotFoundException nếu không tìm thấy
     */
    @Override
    public OpportunityResult execute(Long id) {
        OpportunityResult result = OpportunityCommandMapper.toResult(
                repo.findById(id).orElseThrow(() -> new NotFoundException("Opportunity not found: " + id)));
        List<OpportunityResult> one = List.of(result);
        NameEnricher.apply(one, OpportunityResult::getCustomerId, names::customers, OpportunityResult::setCustomerName);
        NameEnricher.apply(one, OpportunityResult::getContactId, names::contacts, OpportunityResult::setContactName);
        NameEnricher.apply(one, OpportunityResult::getOwnerId, names::users, OpportunityResult::setOwnerName);
        NameEnricher.apply(one, OpportunityResult::getStageId, names::stages, OpportunityResult::setStageName);
        NameEnricher.apply(one, OpportunityResult::getCampaignId, names::campaigns, OpportunityResult::setCampaignName);
        NameEnricher.apply(one, OpportunityResult::getCreatedBy, names::users, OpportunityResult::setCreatedByName);
        NameEnricher.apply(one, OpportunityResult::getUpdatedBy, names::users, OpportunityResult::setUpdatedByName);
        return result;
    }
}
