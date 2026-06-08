package vn.com.be_crm.application.opportunity.query;

import vn.com.be_crm.application.opportunity.dto.OpportunityResult;
import vn.com.be_crm.application.opportunity.mapper.OpportunityCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case lấy cơ hội bán hàng theo ID. */
public class GetOpportunityUseCase implements IUseCase<Long, OpportunityResult> {
    private final IOpportunityRepository repo;
    /** @param repo port lưu trữ */
    public GetOpportunityUseCase(IOpportunityRepository repo) { this.repo = repo; }

    /**
     * Lấy Opportunity theo ID.
     * @param id ID @return OpportunityResult
     * @throws NotFoundException nếu không tìm thấy
     */
    @Override
    public OpportunityResult execute(Long id) {
        return OpportunityCommandMapper.toResult(
                repo.findById(id).orElseThrow(() -> new NotFoundException("Opportunity not found: " + id)));
    }
}
