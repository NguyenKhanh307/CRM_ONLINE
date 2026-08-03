package vn.com.be_crm.application.opportunity.query;

import vn.com.be_crm.application.opportunity.dto.OpportunityStageResult;
import vn.com.be_crm.application.opportunity.mapper.OpportunityStageCommandMapper;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityStageRepository;
import vn.com.be_crm.core.error.frontend.NotFoundException;

/** Use case lấy giai đoạn pipeline theo ID. */
public class GetOpportunityStageUseCase implements IUseCase<Long, OpportunityStageResult> {
    private final IOpportunityStageRepository repo;
    /** @param repo port lưu trữ */
    public GetOpportunityStageUseCase(IOpportunityStageRepository repo) { this.repo = repo; }
    /** @param id ID @return result @throws NotFoundException */
    @Override public OpportunityStageResult execute(Long id) {
        return repo.findById(id).map(OpportunityStageCommandMapper::toResult)
                .orElseThrow(() -> new NotFoundException("OpportunityStage", id));
    }
}
