package vn.com.be_crm.core.config.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.com.be_crm.core.notify.NotifyAssignmentUseCase;
import vn.com.be_crm.application.opportunity.command.*;
import vn.com.be_crm.application.opportunity.query.*;
import vn.com.be_crm.core.tx.port.ITransactionRunner;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityItemRepository;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityRepository;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityStageRepository;

/**
 * Wire các UseCase của module Opportunity (stage, opportunity, item, trash, handover, import) qua @Bean.
 */
@Configuration
public class OpportunityBeanConfig {

    // ===== Opportunity Stage =====

    /** @return CreateOpportunityStageUseCase */
    @Bean public CreateOpportunityStageUseCase createOpportunityStageUseCase(IOpportunityStageRepository r) { return new CreateOpportunityStageUseCase(r); }
    /** @return UpdateOpportunityStageUseCase */
    @Bean public UpdateOpportunityStageUseCase updateOpportunityStageUseCase(IOpportunityStageRepository r) { return new UpdateOpportunityStageUseCase(r); }
    /** @return DeleteOpportunityStageUseCase */
    @Bean public DeleteOpportunityStageUseCase deleteOpportunityStageUseCase(IOpportunityStageRepository r) { return new DeleteOpportunityStageUseCase(r); }
    /** @return GetOpportunityStageUseCase */
    @Bean public GetOpportunityStageUseCase getOpportunityStageUseCase(IOpportunityStageRepository r) { return new GetOpportunityStageUseCase(r); }
    /** @return ListOpportunityStageUseCase */
    @Bean public ListOpportunityStageUseCase listOpportunityStageUseCase(IOpportunityStageRepository r) { return new ListOpportunityStageUseCase(r); }

    // ===== Opportunity =====

    /** @return CreateOpportunityUseCase */
    @Bean public CreateOpportunityUseCase createOpportunityUseCase(IOpportunityRepository r, IOpportunityStageRepository sr) { return new CreateOpportunityUseCase(r, sr); }
    /** @return UpdateOpportunityUseCase */
    @Bean public UpdateOpportunityUseCase updateOpportunityUseCase(IOpportunityRepository r, IOpportunityStageRepository sr, NotifyAssignmentUseCase n) { return new UpdateOpportunityUseCase(r, sr, n); }
    /** @return DeleteOpportunityUseCase */
    @Bean public DeleteOpportunityUseCase deleteOpportunityUseCase(IOpportunityRepository r) { return new DeleteOpportunityUseCase(r); }
    /** @return GetOpportunityUseCase */
    @Bean public GetOpportunityUseCase getOpportunityUseCase(IOpportunityRepository r, vn.com.be_crm.core.lookup.port.INameResolver n) { return new GetOpportunityUseCase(r, n); }
    /** @return ListOpportunityUseCase */
    @Bean public ListOpportunityUseCase listOpportunityUseCase(IOpportunityRepository r, vn.com.be_crm.core.lookup.port.INameResolver n) { return new ListOpportunityUseCase(r, n); }

    // ===== Kanban (bảng giai đoạn) =====

    /** @return ChangeOpportunityStageUseCase — đổi giai đoạn khi kéo-thả thẻ */
    @Bean public ChangeOpportunityStageUseCase changeOpportunityStageUseCase(IOpportunityRepository r, IOpportunityStageRepository sr) { return new ChangeOpportunityStageUseCase(r, sr); }
    /** @return GetOpportunityBoardUseCase — nạp cột + thẻ của bảng Kanban */
    @Bean public GetOpportunityBoardUseCase getOpportunityBoardUseCase(vn.com.be_crm.domain.opportunity.repository.IOpportunityBoardRepository r) { return new GetOpportunityBoardUseCase(r); }

    // ===== Opportunity Item =====

    /** @return CreateOpportunityItemUseCase */
    @Bean public CreateOpportunityItemUseCase createOpportunityItemUseCase(IOpportunityItemRepository r) { return new CreateOpportunityItemUseCase(r); }
    /** @return UpdateOpportunityItemUseCase */
    @Bean public UpdateOpportunityItemUseCase updateOpportunityItemUseCase(IOpportunityItemRepository r) { return new UpdateOpportunityItemUseCase(r); }
    /** @return DeleteOpportunityItemUseCase */
    @Bean public DeleteOpportunityItemUseCase deleteOpportunityItemUseCase(IOpportunityItemRepository r) { return new DeleteOpportunityItemUseCase(r); }
    /** @return ListOpportunityItemUseCase */
    @Bean public ListOpportunityItemUseCase listOpportunityItemUseCase(IOpportunityItemRepository r) { return new ListOpportunityItemUseCase(r); }
    /** @return RecomputeOpportunityAmountUseCase — roll-up giá trị cơ hội từ dòng hàng */
    @Bean public RecomputeOpportunityAmountUseCase recomputeOpportunityAmountUseCase(IOpportunityRepository r, IOpportunityItemRepository ir) {
        return new RecomputeOpportunityAmountUseCase(r, ir);
    }

    // ===== Trash =====

    /** @return ListDeletedOpportunitiesUseCase */
    @Bean public ListDeletedOpportunitiesUseCase listDeletedOpportunitiesUseCase(IOpportunityRepository r) { return new ListDeletedOpportunitiesUseCase(r); }
    /** @return RestoreOpportunityUseCase */
    @Bean public RestoreOpportunityUseCase restoreOpportunityUseCase(IOpportunityRepository r) { return new RestoreOpportunityUseCase(r); }
    /** @return PurgeOpportunityUseCase */
    @Bean public PurgeOpportunityUseCase purgeOpportunityUseCase(IOpportunityRepository r) { return new PurgeOpportunityUseCase(r); }

    // ===== Handover & Import =====

    /** @return HandoverBulkOpportunityUseCase */
    @Bean public HandoverBulkOpportunityUseCase handoverBulkOpportunityUseCase(IOpportunityRepository r, NotifyAssignmentUseCase n) { return new HandoverBulkOpportunityUseCase(r, n); }
    /** @return ImportBulkOpportunityUseCase */
    @Bean public ImportBulkOpportunityUseCase importBulkOpportunityUseCase(IOpportunityRepository r, ITransactionRunner tx) { return new ImportBulkOpportunityUseCase(r, tx); }
}
