package vn.com.be_crm.application.opportunity.command;

import vn.com.be_crm.application.opportunity.dto.CreateOpportunityItemCommand;
import vn.com.be_crm.application.opportunity.dto.OpportunityItemResult;
import vn.com.be_crm.application.opportunity.mapper.OpportunityItemCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityItemRepository;

/** Use case tạo mới dòng sản phẩm trong cơ hội. */
public class CreateOpportunityItemUseCase implements IUseCase<CreateOpportunityItemCommand, OpportunityItemResult> {
    private final IOpportunityItemRepository repo;
    /** @param repo port lưu trữ */
    public CreateOpportunityItemUseCase(IOpportunityItemRepository repo) { this.repo = repo; }

    /**
     * Tạo mới OpportunityItem và trả về result.
     * @param cmd dữ liệu tạo mới @return OpportunityItemResult
     */
    @Override
    public OpportunityItemResult execute(CreateOpportunityItemCommand cmd) {
        return OpportunityItemCommandMapper.toResult(repo.save(OpportunityItemCommandMapper.toEntity(cmd)));
    }
}
