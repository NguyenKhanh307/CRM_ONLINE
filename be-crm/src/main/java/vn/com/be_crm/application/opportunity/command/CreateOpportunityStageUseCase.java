package vn.com.be_crm.application.opportunity.command;

import vn.com.be_crm.application.opportunity.dto.*;
import vn.com.be_crm.application.opportunity.mapper.OpportunityStageCommandMapper;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityStageRepository;

/** Use case tạo mới giai đoạn pipeline. */
public class CreateOpportunityStageUseCase implements IUseCase<CreateOpportunityStageCommand, OpportunityStageResult> {
    private final IOpportunityStageRepository repo;
    /** @param repo port lưu trữ */
    public CreateOpportunityStageUseCase(IOpportunityStageRepository repo) { this.repo = repo; }
    /** @param c command @return result */
    @Override public OpportunityStageResult execute(CreateOpportunityStageCommand c) {
        return OpportunityStageCommandMapper.toResult(repo.save(OpportunityStageCommandMapper.toEntity(c)));
    }
}
