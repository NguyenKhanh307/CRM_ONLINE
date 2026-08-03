package vn.com.be_crm.application.opportunity.command;

import vn.com.be_crm.application.opportunity.dto.OpportunityResult;
import vn.com.be_crm.application.opportunity.dto.UpdateOpportunityCommand;
import vn.com.be_crm.application.opportunity.mapper.OpportunityCommandMapper;
import vn.com.be_crm.core.notify.NotifyAssignmentUseCase;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.opportunity.entity.Opportunity;
import vn.com.be_crm.domain.opportunity.entity.OpportunityStage;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityRepository;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityStageRepository;
import vn.com.be_crm.core.error.frontend.NotFoundException;

import java.util.Objects;

/** Use case cập nhật cơ hội bán hàng. Trạng thái và xác suất thắng suy ra tự động từ giai đoạn pipeline. */
public class UpdateOpportunityUseCase implements IUseCase<UpdateOpportunityCommand, OpportunityResult> {
    private final IOpportunityRepository repo;
    private final IOpportunityStageRepository stageRepo;
    private final NotifyAssignmentUseCase notifyUC;
    /** @param repo port lưu trữ cơ hội @param stageRepo port lưu trữ giai đoạn pipeline @param notifyUC báo cho người phụ trách mới */
    public UpdateOpportunityUseCase(IOpportunityRepository repo, IOpportunityStageRepository stageRepo,
                                    NotifyAssignmentUseCase notifyUC) {
        this.repo = repo;
        this.stageRepo = stageRepo;
        this.notifyUC = notifyUC;
    }

    /**
     * Cập nhật Opportunity và trả về result. Trạng thái và xác suất suy ra từ giai đoạn sau cập nhật.
     * @param cmd dữ liệu cập nhật @return OpportunityResult
     * @throws NotFoundException nếu không tìm thấy
     */
    @Override
    public OpportunityResult execute(UpdateOpportunityCommand cmd) {
        Opportunity existing = repo.findById(cmd.getId())
                .orElseThrow(() -> new NotFoundException("Opportunity not found: " + cmd.getId()));
        Long stageId = cmd.getStageId() != null ? cmd.getStageId() : existing.getStageId();
        OpportunityStage stage = stageId == null ? null : stageRepo.findById(stageId).orElse(null);
        Opportunity saved = repo.save(OpportunityCommandMapper.toEntity(cmd, existing, stage));
        // Đổi người phụ trách → báo cho người nhận việc
        if (!Objects.equals(existing.getOwnerId(), saved.getOwnerId())) {
            notifyUC.notifyAssigned("opportunity", "cơ hội", saved.getOwnerId(), saved.getId(), saved.getCode());
        }
        return OpportunityCommandMapper.toResult(saved);
    }
}
