package vn.com.be_crm.infrastructure.shared.config.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.com.be_crm.application.activity.command.*;
import vn.com.be_crm.application.activity.query.*;
import vn.com.be_crm.domain.activity.repository.IActivityRepository;

/**
 * Wire các UseCase của module Activity (CRUD + import) qua @Bean.
 */
@Configuration
public class ActivityBeanConfig {

    /** @return CreateActivityUseCase được inject IActivityRepository */
    @Bean public CreateActivityUseCase createActivityUseCase(IActivityRepository r) { return new CreateActivityUseCase(r); }
    /** @return UpdateActivityUseCase được inject IActivityRepository */
    @Bean public UpdateActivityUseCase updateActivityUseCase(IActivityRepository r) { return new UpdateActivityUseCase(r); }
    /** @return DeleteActivityUseCase được inject IActivityRepository */
    @Bean public DeleteActivityUseCase deleteActivityUseCase(IActivityRepository r) { return new DeleteActivityUseCase(r); }
    /** @return GetActivityUseCase được inject IActivityRepository */
    @Bean public GetActivityUseCase getActivityUseCase(IActivityRepository r) { return new GetActivityUseCase(r); }
    /** @return ListActivityUseCase được inject IActivityRepository */
    @Bean public ListActivityUseCase listActivityUseCase(IActivityRepository r) { return new ListActivityUseCase(r); }
    /** @return ImportBulkActivityUseCase được inject IActivityRepository */
    @Bean public ImportBulkActivityUseCase importBulkActivityUseCase(IActivityRepository r) { return new ImportBulkActivityUseCase(r); }
}
