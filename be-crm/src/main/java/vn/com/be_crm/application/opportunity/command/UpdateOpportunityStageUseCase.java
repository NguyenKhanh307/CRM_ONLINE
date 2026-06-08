package vn.com.be_crm.application.opportunity.command;

import vn.com.be_crm.application.opportunity.dto.*;
import vn.com.be_crm.application.opportunity.mapper.OpportunityStageCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityStageRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case cập nhật giai đoạn pipeline. */
public class UpdateOpportunityStageUseCase implements IUseCase<UpdateOpportunityStageCommand, OpportunityStageResult> {
    private final IOpportunityStageRepository repo;
    /** @param repo port lưu trữ */
    public UpdateOpportunityStageUseCase(IOpportunityStageRepository repo) { this.repo = repo; }
    /** @param c command @return result @throws NotFoundException */
    @Override public OpportunityStageResult execute(UpdateOpportunityStageCommand c) {
        var e = repo.findById(c.getId()).orElseThrow(() -> new NotFoundException("OpportunityStage", c.getId()));
        return OpportunityStageCommandMapper.toResult(repo.save(OpportunityStageCommandMapper.toEntity(c, e)));
    }
}
