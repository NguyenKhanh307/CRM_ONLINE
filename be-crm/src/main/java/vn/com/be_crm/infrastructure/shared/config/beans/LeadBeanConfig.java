package vn.com.be_crm.infrastructure.shared.config.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.com.be_crm.application.lead.command.*;
import vn.com.be_crm.application.lead.query.*;
import vn.com.be_crm.domain.lead.repository.ILeadActivityRepository;
import vn.com.be_crm.domain.lead.repository.ILeadRepository;
import vn.com.be_crm.domain.lead.repository.ILeadTransferRepository;

/**
 * Wire các UseCase của module Lead (lead, activity, transfer, trash, handover, import) qua @Bean.
 */
@Configuration
public class LeadBeanConfig {

    // ===== Lead =====

    /** @return CreateLeadUseCase */
    @Bean public CreateLeadUseCase createLeadUseCase(ILeadRepository r) { return new CreateLeadUseCase(r); }
    /** @return UpdateLeadUseCase */
    @Bean public UpdateLeadUseCase updateLeadUseCase(ILeadRepository r) { return new UpdateLeadUseCase(r); }
    /** @return DeleteLeadUseCase */
    @Bean public DeleteLeadUseCase deleteLeadUseCase(ILeadRepository r) { return new DeleteLeadUseCase(r); }
    /** @return GetLeadUseCase */
    @Bean public GetLeadUseCase getLeadUseCase(ILeadRepository r) { return new GetLeadUseCase(r); }
    /** @return ListLeadUseCase */
    @Bean public ListLeadUseCase listLeadUseCase(ILeadRepository r) { return new ListLeadUseCase(r); }

    // ===== Lead Activity =====

    /** @return CreateLeadActivityUseCase */
    @Bean public CreateLeadActivityUseCase createLeadActivityUseCase(ILeadActivityRepository r) { return new CreateLeadActivityUseCase(r); }
    /** @return UpdateLeadActivityUseCase */
    @Bean public UpdateLeadActivityUseCase updateLeadActivityUseCase(ILeadActivityRepository r) { return new UpdateLeadActivityUseCase(r); }
    /** @return DeleteLeadActivityUseCase */
    @Bean public DeleteLeadActivityUseCase deleteLeadActivityUseCase(ILeadActivityRepository r) { return new DeleteLeadActivityUseCase(r); }
    /** @return ListLeadActivityUseCase */
    @Bean public ListLeadActivityUseCase listLeadActivityUseCase(ILeadActivityRepository r) { return new ListLeadActivityUseCase(r); }

    // ===== Lead Transfer =====

    /** @return CreateLeadTransferUseCase */
    @Bean public CreateLeadTransferUseCase createLeadTransferUseCase(ILeadTransferRepository r) { return new CreateLeadTransferUseCase(r); }
    /** @return UpdateLeadTransferUseCase */
    @Bean public UpdateLeadTransferUseCase updateLeadTransferUseCase(ILeadTransferRepository r) { return new UpdateLeadTransferUseCase(r); }
    /** @return DeleteLeadTransferUseCase */
    @Bean public DeleteLeadTransferUseCase deleteLeadTransferUseCase(ILeadTransferRepository r) { return new DeleteLeadTransferUseCase(r); }
    /** @return ListLeadTransferUseCase */
    @Bean public ListLeadTransferUseCase listLeadTransferUseCase(ILeadTransferRepository r) { return new ListLeadTransferUseCase(r); }

    // ===== Trash =====

    /** @return ListDeletedLeadsUseCase */
    @Bean public ListDeletedLeadsUseCase listDeletedLeadsUseCase(ILeadRepository r) { return new ListDeletedLeadsUseCase(r); }
    /** @return RestoreLeadUseCase */
    @Bean public RestoreLeadUseCase restoreLeadUseCase(ILeadRepository r) { return new RestoreLeadUseCase(r); }
    /** @return PurgeLeadUseCase */
    @Bean public PurgeLeadUseCase purgeLeadUseCase(ILeadRepository r) { return new PurgeLeadUseCase(r); }

    // ===== Handover & Import =====

    /** @return HandoverBulkLeadUseCase */
    @Bean public HandoverBulkLeadUseCase handoverBulkLeadUseCase(ILeadRepository r) { return new HandoverBulkLeadUseCase(r); }
    /** @return ImportBulkLeadUseCase */
    @Bean public ImportBulkLeadUseCase importBulkLeadUseCase(ILeadRepository r) { return new ImportBulkLeadUseCase(r); }
}
