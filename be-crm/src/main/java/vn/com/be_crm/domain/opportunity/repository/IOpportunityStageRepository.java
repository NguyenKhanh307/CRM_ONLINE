package vn.com.be_crm.domain.opportunity.repository;

import vn.com.be_crm.core.page.PageRequest;
import vn.com.be_crm.core.page.PageResult;
import vn.com.be_crm.domain.opportunity.entity.OpportunityStage;

import java.util.Optional;

/** Port lưu trữ cho OpportunityStage. */
public interface IOpportunityStageRepository {
    OpportunityStage save(OpportunityStage s);
    Optional<OpportunityStage> findById(Long id);
    void deleteById(Long id);
    PageResult<OpportunityStage> findAll(PageRequest r);
}
