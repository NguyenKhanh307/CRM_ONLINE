package vn.com.be_crm.application.opportunity.command;

import vn.com.be_crm.application.opportunity.dto.OpportunityResult;
import vn.com.be_crm.application.opportunity.dto.UpdateOpportunityCommand;
import vn.com.be_crm.application.opportunity.mapper.OpportunityCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.opportunity.entity.Opportunity;
import vn.com.be_crm.domain.opportunity.entity.OpportunityStage;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityRepository;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityStageRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case cập nhật cơ hội bán hàng. Trạng thái và xác suất thắng suy ra tự động từ giai đoạn pipeline. */
public class UpdateOpportunityUseCase implements IUseCase<UpdateOpportunityCommand, OpportunityResult> {
    private final IOpportunityRepository repo;
    private final IOpportunityStageRepository stageRepo;
    /** @param repo port lưu trữ cơ hội @param stageRepo port lưu trữ giai đoạn pipeline */
    public UpdateOpportunityUseCase(IOpportunityRepository repo, IOpportunityStageRepository stageRepo) {
        this.repo = repo;
        this.stageRepo = stageRepo;
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
        return OpportunityCommandMapper.toResult(repo.save(OpportunityCommandMapper.toEntity(cmd, existing, stage)));
    }
}
