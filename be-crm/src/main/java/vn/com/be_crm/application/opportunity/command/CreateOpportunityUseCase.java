package vn.com.be_crm.application.opportunity.command;

import vn.com.be_crm.application.opportunity.dto.CreateOpportunityCommand;
import vn.com.be_crm.application.opportunity.dto.OpportunityResult;
import vn.com.be_crm.application.opportunity.mapper.OpportunityCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityRepository;

/** Use case tạo mới cơ hội bán hàng. */
public class CreateOpportunityUseCase implements IUseCase<CreateOpportunityCommand, OpportunityResult> {
    private final IOpportunityRepository repo;
    /** @param repo port lưu trữ */
    public CreateOpportunityUseCase(IOpportunityRepository repo) { this.repo = repo; }

    /**
     * Tạo mới Opportunity và trả về result.
     * @param cmd dữ liệu tạo mới @return OpportunityResult
     */
    @Override
    public OpportunityResult execute(CreateOpportunityCommand cmd) {
        return OpportunityCommandMapper.toResult(repo.save(OpportunityCommandMapper.toEntity(cmd)));
    }
}
