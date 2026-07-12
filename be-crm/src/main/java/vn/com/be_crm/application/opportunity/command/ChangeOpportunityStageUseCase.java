package vn.com.be_crm.application.opportunity.command;

import vn.com.be_crm.application.opportunity.dto.ChangeOpportunityStageCommand;
import vn.com.be_crm.application.opportunity.dto.OpportunityResult;
import vn.com.be_crm.application.opportunity.dto.UpdateOpportunityCommand;
import vn.com.be_crm.application.opportunity.mapper.OpportunityCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.opportunity.entity.Opportunity;
import vn.com.be_crm.domain.opportunity.entity.OpportunityStage;
import vn.com.be_crm.domain.opportunity.enums.OpportunityStatus;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityRepository;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityStageRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/**
 * Use case đổi giai đoạn pipeline của một cơ hội — hành động của bảng Kanban (kéo thẻ sang cột khác).
 * Trạng thái vẫn suy ra tự động qua {@link OpportunityStatus#fromStage} (không nhân bản quy tắc);
 * khác {@code UpdateOpportunityUseCase} ở chỗ giai đoạn không tồn tại thì ném 404 thay vì âm thầm về {@code open}.
 */
public class ChangeOpportunityStageUseCase implements IUseCase<ChangeOpportunityStageCommand, OpportunityResult> {

    private final IOpportunityRepository repo;
    private final IOpportunityStageRepository stageRepo;

    /** @param repo port lưu trữ cơ hội @param stageRepo port lưu trữ giai đoạn pipeline */
    public ChangeOpportunityStageUseCase(IOpportunityRepository repo, IOpportunityStageRepository stageRepo) {
        this.repo = repo;
        this.stageRepo = stageRepo;
    }

    /**
     * Đổi giai đoạn và suy lại trạng thái.
     *
     * @param cmd lệnh đổi giai đoạn
     * @return cơ hội sau khi cập nhật
     * @throws NotFoundException nếu không tìm thấy cơ hội hoặc giai đoạn
     */
    @Override
    public OpportunityResult execute(ChangeOpportunityStageCommand cmd) {
        Opportunity existing = repo.findById(cmd.getId())
                .orElseThrow(() -> new NotFoundException("Opportunity not found: " + cmd.getId()));
        OpportunityStage stage = stageRepo.findById(cmd.getStageId())
                .orElseThrow(() -> new NotFoundException("OpportunityStage not found: " + cmd.getStageId()));

        UpdateOpportunityCommand update = UpdateOpportunityCommand.builder()
                .id(cmd.getId())
                .stageId(cmd.getStageId())
                .winLossReason(cmd.getWinLossReason())
                .build();
        // Mapper null-coalescing: mọi field không truyền đều giữ nguyên giá trị cũ.
        return OpportunityCommandMapper.toResult(
                repo.save(OpportunityCommandMapper.toEntity(update, existing, OpportunityStatus.fromStage(stage))));
    }
}
