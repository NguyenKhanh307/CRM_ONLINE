package vn.com.be_crm.core.config.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.com.be_crm.application.activity.command.*;
import vn.com.be_crm.application.activity.query.*;
import vn.com.be_crm.application.lead.command.AddLeadScoreUseCase;
import vn.com.be_crm.core.tx.port.ITransactionRunner;
import vn.com.be_crm.domain.activity.repository.IActivityRepository;

/**
 * Wire các UseCase của module Activity (CRUD + import) qua @Bean.
 */
@Configuration
public class ActivityBeanConfig {

    /** @return CreateActivityUseCase được inject IActivityRepository + AddLeadScoreUseCase */
    @Bean public CreateActivityUseCase createActivityUseCase(IActivityRepository r, AddLeadScoreUseCase a) { return new CreateActivityUseCase(r, a); }
    /** @return UpdateActivityUseCase được inject IActivityRepository */
    @Bean public UpdateActivityUseCase updateActivityUseCase(IActivityRepository r) { return new UpdateActivityUseCase(r); }
    /** @return DeleteActivityUseCase được inject IActivityRepository */
    @Bean public DeleteActivityUseCase deleteActivityUseCase(IActivityRepository r) { return new DeleteActivityUseCase(r); }
    /** @return GetActivityUseCase được inject IActivityRepository */
    @Bean public GetActivityUseCase getActivityUseCase(IActivityRepository r) { return new GetActivityUseCase(r); }
    /** @return ListActivityUseCase được inject IActivityRepository */
    @Bean public ListActivityUseCase listActivityUseCase(IActivityRepository r, vn.com.be_crm.core.lookup.port.INameResolver n) { return new ListActivityUseCase(r, n); }
    /** @return ImportBulkActivityUseCase được inject IActivityRepository */
    @Bean public ImportBulkActivityUseCase importBulkActivityUseCase(IActivityRepository r, ITransactionRunner tx) { return new ImportBulkActivityUseCase(r, tx); }
    /** @return ActivityWorkflowUseCase — hành động start/complete/cancel */
    @Bean public ActivityWorkflowUseCase activityWorkflowUseCase(IActivityRepository r) { return new ActivityWorkflowUseCase(r); }
}
