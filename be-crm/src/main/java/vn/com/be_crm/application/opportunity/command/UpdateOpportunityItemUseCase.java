package vn.com.be_crm.application.opportunity.command;

import vn.com.be_crm.application.opportunity.dto.OpportunityItemResult;
import vn.com.be_crm.application.opportunity.dto.UpdateOpportunityItemCommand;
import vn.com.be_crm.application.opportunity.mapper.OpportunityItemCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.opportunity.entity.OpportunityItem;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityItemRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case cập nhật dòng sản phẩm trong cơ hội. */
public class UpdateOpportunityItemUseCase implements IUseCase<UpdateOpportunityItemCommand, OpportunityItemResult> {
    private final IOpportunityItemRepository repo;
    /** @param repo port lưu trữ */
    public UpdateOpportunityItemUseCase(IOpportunityItemRepository repo) { this.repo = repo; }

    /**
     * Cập nhật OpportunityItem và trả về result.
     * @param cmd dữ liệu cập nhật @return OpportunityItemResult
     * @throws NotFoundException nếu không tìm thấy
     */
    @Override
    public OpportunityItemResult execute(UpdateOpportunityItemCommand cmd) {
        OpportunityItem existing = repo.findById(cmd.getId())
                .orElseThrow(() -> new NotFoundException("OpportunityItem not found: " + cmd.getId()));
        return OpportunityItemCommandMapper.toResult(repo.save(OpportunityItemCommandMapper.toEntity(cmd, existing)));
    }
}
