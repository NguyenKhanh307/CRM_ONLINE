package vn.com.be_crm.application.opportunity.command;

import vn.com.be_crm.application.opportunity.dto.CreateOpportunityCommand;
import vn.com.be_crm.application.opportunity.dto.OpportunityResult;
import vn.com.be_crm.application.opportunity.mapper.OpportunityCommandMapper;
import vn.com.be_crm.application.opportunity.mapper.OpportunityItemCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.shared.exception.DomainException;
import vn.com.be_crm.application.shared.util.LineItemTotals;
import vn.com.be_crm.domain.opportunity.entity.OpportunityItem;
import vn.com.be_crm.domain.opportunity.entity.OpportunityStage;
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
     * Trạng thái và xác suất thắng suy ra tự động từ giai đoạn pipeline được chọn.
     * @param cmd dữ liệu tạo mới @return OpportunityResult
     */
    @Override
    public OpportunityResult execute(CreateOpportunityCommand cmd) {
        // Check trùng thân thiện trước khi insert (DB unique constraint là lớp phòng thủ 2)
        if (cmd.getCode() != null && repo.findByCode(cmd.getCode()).isPresent()) {
            throw new DomainException("Mã cơ hội \"" + cmd.getCode() + "\" đã tồn tại, vui lòng dùng mã khác");
        }
        OpportunityStage stage = resolveStage(cmd.getStageId());
        if (cmd.getItems() != null && !cmd.getItems().isEmpty()) {
            List<OpportunityItem> items = cmd.getItems().stream()
                    .map(OpportunityItemCommandMapper::toEntity).collect(Collectors.toList());
            // Roll-up: giá trị cơ hội = tổng thành tiền các dòng hàng (không nhận amount thủ công từ FE)
            var entity = OpportunityCommandMapper.toEntity(cmd, stage).toBuilder()
                    .amount(LineItemTotals.sumAmount(items, OpportunityItem::getAmount)).build();
            return OpportunityCommandMapper.toResult(repo.saveWithItems(entity, items));
        }
        return OpportunityCommandMapper.toResult(repo.save(OpportunityCommandMapper.toEntity(cmd, stage)));
    }

    /**
     * Nạp giai đoạn pipeline được chọn — nguồn suy ra cả trạng thái lẫn xác suất thắng.
     * @param stageId ID giai đoạn (có thể null) @return giai đoạn, hoặc null nếu chưa chọn / không tồn tại
     */
    private OpportunityStage resolveStage(Long stageId) {
        if (stageId == null) return null;
        return stageRepo.findById(stageId).orElse(null);
    }
}
