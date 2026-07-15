package vn.com.be_crm.infrastructure.shared.config.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.com.be_crm.application.lead.command.*;
import vn.com.be_crm.application.lead.query.*;
import vn.com.be_crm.application.notification.command.CreateNotificationUseCase;
import vn.com.be_crm.domain.contact.repository.IContactRepository;
import vn.com.be_crm.domain.customer.repository.ICustomerRepository;
import vn.com.be_crm.domain.lead.repository.ILeadRepository;
import vn.com.be_crm.domain.lead.repository.ILeadTrackingEventRepository;
import vn.com.be_crm.domain.lead.repository.ILeadTransferRepository;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityRepository;
import vn.com.be_crm.application.shared.tx.ITransactionRunner;

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
    @Bean public GetLeadUseCase getLeadUseCase(ILeadRepository r, vn.com.be_crm.application.shared.lookup.INameResolver n) { return new GetLeadUseCase(r, n); }
    /** @return ListLeadUseCase */
    @Bean public ListLeadUseCase listLeadUseCase(ILeadRepository r, vn.com.be_crm.application.shared.lookup.INameResolver n) { return new ListLeadUseCase(r, n); }
    /** @return LeadWorkflowUseCase — convert (tách KH+LH+CH, 1 transaction) / lose */
    @Bean public LeadWorkflowUseCase leadWorkflowUseCase(ILeadRepository r, ICustomerRepository cr,
                                                         IContactRepository ctr, IOpportunityRepository or,
                                                         ITransactionRunner tx) {
        return new LeadWorkflowUseCase(r, cr, ctr, or, tx);
    }

    // ===== Lead Scoring & Web Tracking =====

    /** @return AddLeadScoreUseCase — cộng điểm dùng chung (tracking + activity) */
    @Bean public AddLeadScoreUseCase addLeadScoreUseCase(ILeadRepository r, CreateNotificationUseCase n,
                                                         vn.com.be_crm.application.shared.notify.IManagerResolver mr,
                                                         ITransactionRunner tx) {
        return new AddLeadScoreUseCase(r, n, mr, tx);
    }
    /** @return TrackVisitUseCase */
    @Bean public TrackVisitUseCase trackVisitUseCase(ILeadRepository r) { return new TrackVisitUseCase(r); }
    /** @return RecordTrackingEventUseCase */
    @Bean public RecordTrackingEventUseCase recordTrackingEventUseCase(ILeadRepository r, ILeadTrackingEventRepository e, AddLeadScoreUseCase a,
                                                                       ITransactionRunner tx) {
        return new RecordTrackingEventUseCase(r, e, a, tx);
    }
    /** @return SubmitTrackingFormUseCase */
    @Bean public SubmitTrackingFormUseCase submitTrackingFormUseCase(ILeadRepository r, ILeadTrackingEventRepository e, AddLeadScoreUseCase a,
                                                                     ITransactionRunner tx) {
        return new SubmitTrackingFormUseCase(r, e, a, tx);
    }

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
