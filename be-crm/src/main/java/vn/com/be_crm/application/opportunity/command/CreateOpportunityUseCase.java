package vn.com.be_crm.application.opportunity.command;

import vn.com.be_crm.application.opportunity.dto.CreateOpportunityCommand;
import vn.com.be_crm.application.opportunity.dto.OpportunityResult;
import vn.com.be_crm.application.opportunity.mapper.OpportunityCommandMapper;
import vn.com.be_crm.application.opportunity.mapper.OpportunityItemCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityRepository;

import java.util.stream.Collectors;

/** Use case tạo mới cơ hội bán hàng (kèm dòng hàng nếu có). */
public class CreateOpportunityUseCase implements IUseCase<CreateOpportunityCommand, OpportunityResult> {
    private final IOpportunityRepository repo;
    /** @param repo port lưu trữ */
    public CreateOpportunityUseCase(IOpportunityRepository repo) { this.repo = repo; }

    /**
     * Tạo mới Opportunity; nếu có items thì lưu header + dòng hàng trong một transaction.
     * @param cmd dữ liệu tạo mới @return OpportunityResult
     */
    @Override
    public OpportunityResult execute(CreateOpportunityCommand cmd) {
        var entity = OpportunityCommandMapper.toEntity(cmd);
        if (cmd.getItems() != null && !cmd.getItems().isEmpty()) {
            var items = cmd.getItems().stream()
                    .map(OpportunityItemCommandMapper::toEntity).collect(Collectors.toList());
            return OpportunityCommandMapper.toResult(repo.saveWithItems(entity, items));
        }
        return OpportunityCommandMapper.toResult(repo.save(entity));
    }
}
