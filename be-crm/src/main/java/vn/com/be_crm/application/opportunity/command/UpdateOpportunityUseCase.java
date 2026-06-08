package vn.com.be_crm.application.opportunity.command;

import vn.com.be_crm.application.opportunity.dto.OpportunityResult;
import vn.com.be_crm.application.opportunity.dto.UpdateOpportunityCommand;
import vn.com.be_crm.application.opportunity.mapper.OpportunityCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.opportunity.entity.Opportunity;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case cập nhật cơ hội bán hàng. */
public class UpdateOpportunityUseCase implements IUseCase<UpdateOpportunityCommand, OpportunityResult> {
    private final IOpportunityRepository repo;
    /** @param repo port lưu trữ */
    public UpdateOpportunityUseCase(IOpportunityRepository repo) { this.repo = repo; }

    /**
     * Cập nhật Opportunity và trả về result.
     * @param cmd dữ liệu cập nhật @return OpportunityResult
     * @throws NotFoundException nếu không tìm thấy
     */
    @Override
    public OpportunityResult execute(UpdateOpportunityCommand cmd) {
        Opportunity existing = repo.findById(cmd.getId())
                .orElseThrow(() -> new NotFoundException("Opportunity not found: " + cmd.getId()));
        return OpportunityCommandMapper.toResult(repo.save(OpportunityCommandMapper.toEntity(cmd, existing)));
    }
}
