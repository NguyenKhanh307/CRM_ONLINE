package vn.com.be_crm.application.opportunity.command;

import vn.com.be_crm.application.opportunity.dto.CreateOpportunityCommand;
import vn.com.be_crm.application.opportunity.dto.OpportunityResult;
import vn.com.be_crm.application.opportunity.mapper.OpportunityCommandMapper;
import vn.com.be_crm.application.opportunity.mapper.OpportunityItemCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.application.shared.util.LineItemTotals;
import vn.com.be_crm.domain.opportunity.entity.OpportunityItem;
import vn.com.be_crm.domain.opportunity.enums.OpportunityStatus;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityRepository;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityStageRepository;

import java.util.List;
import java.util.stream.Collectors;

/** Use case tạo mới cơ hội bán hàng (kèm dòng hàng nếu có). */
public class CreateOpportunityUseCase implements IUseCase<CreateOpportunityCommand, OpportunityResult> {
    private final IOpportunityRepository repo;
    private final IOpportunityStageRepository stageRepo;
    /** @param repo port lưu trữ cơ hội @param stageRepo port lưu trữ giai đoạn pipeline */
    public CreateOpportunityUseCase(IOpportunityRepository repo, IOpportunityStageRepository stageRepo) {
        this.repo = repo;
        this.stageRepo = stageRepo;
    }

    /**
     * Tạo mới Opportunity; nếu có items thì lưu header + dòng hàng trong một transaction.
     * Trạng thái suy ra tự động từ giai đoạn pipeline được chọn.
     * @param cmd dữ liệu tạo mới @return OpportunityResult
     */
    @Override
    public OpportunityResult execute(CreateOpportunityCommand cmd) {
        OpportunityStatus status = deriveStatus(cmd.getStageId());
        if (cmd.getItems() != null && !cmd.getItems().isEmpty()) {
            List<OpportunityItem> items = cmd.getItems().stream()
                    .map(OpportunityItemCommandMapper::toEntity).collect(Collectors.toList());
            // Roll-up: giá trị cơ hội = tổng thành tiền các dòng hàng (không nhận amount thủ công từ FE)
            var entity = OpportunityCommandMapper.toEntity(cmd, status).toBuilder()
                    .amount(LineItemTotals.sumAmount(items, OpportunityItem::getAmount)).build();
            return OpportunityCommandMapper.toResult(repo.saveWithItems(entity, items));
        }
        return OpportunityCommandMapper.toResult(repo.save(OpportunityCommandMapper.toEntity(cmd, status)));
    }

    /**
     * Suy ra trạng thái cơ hội từ giai đoạn pipeline (null stage → open).
     * @param stageId ID giai đoạn (có thể null) @return trạng thái suy ra
     */
    private OpportunityStatus deriveStatus(Long stageId) {
        if (stageId == null) return OpportunityStatus.open;
        return OpportunityStatus.fromStage(stageRepo.findById(stageId).orElse(null));
    }
}
