package vn.com.be_crm.infrastructure.shared.config.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.com.be_crm.application.customer.command.*;
import vn.com.be_crm.application.customer.query.*;
import vn.com.be_crm.domain.customer.repository.ICustomerRepository;
import vn.com.be_crm.domain.customer.repository.ICustomerShareRepository;

/**
 * Wire các UseCase của module Customer (customer, share, trash, handover, import) qua @Bean.
 */
@Configuration
public class CustomerBeanConfig {

    // ===== Customer =====

    /** @return CreateCustomerUseCase */
    @Bean public CreateCustomerUseCase createCustomerUseCase(ICustomerRepository r) { return new CreateCustomerUseCase(r); }
    /** @return UpdateCustomerUseCase */
    @Bean public UpdateCustomerUseCase updateCustomerUseCase(ICustomerRepository r) { return new UpdateCustomerUseCase(r); }
    /** @return DeleteCustomerUseCase */
    @Bean public DeleteCustomerUseCase deleteCustomerUseCase(ICustomerRepository r) { return new DeleteCustomerUseCase(r); }
    /** @return GetCustomerUseCase */
    @Bean public GetCustomerUseCase getCustomerUseCase(ICustomerRepository r) { return new GetCustomerUseCase(r); }
    /** @return ListCustomerUseCase */
    @Bean public ListCustomerUseCase listCustomerUseCase(ICustomerRepository r, vn.com.be_crm.application.shared.lookup.INameResolver n) { return new ListCustomerUseCase(r, n); }
    /** @return CustomerWorkflowUseCase — hành động activate/deactivate */
    @Bean public CustomerWorkflowUseCase customerWorkflowUseCase(ICustomerRepository r) { return new CustomerWorkflowUseCase(r); }

    // ===== Customer Share =====

    /** @return AssignCustomerShareUseCase */
    @Bean public AssignCustomerShareUseCase assignCustomerShareUseCase(ICustomerShareRepository r) { return new AssignCustomerShareUseCase(r); }
    /** @return RevokeCustomerShareUseCase */
    @Bean public RevokeCustomerShareUseCase revokeCustomerShareUseCase(ICustomerShareRepository r) { return new RevokeCustomerShareUseCase(r); }
    /** @return ListCustomerShareUseCase */
    @Bean public ListCustomerShareUseCase listCustomerShareUseCase(ICustomerShareRepository r) { return new ListCustomerShareUseCase(r); }

    // ===== Trash =====

    /** @return ListDeletedCustomersUseCase */
    @Bean public ListDeletedCustomersUseCase listDeletedCustomersUseCase(ICustomerRepository r) { return new ListDeletedCustomersUseCase(r); }
    /** @return RestoreCustomerUseCase */
    @Bean public RestoreCustomerUseCase restoreCustomerUseCase(ICustomerRepository r) { return new RestoreCustomerUseCase(r); }
    /** @return PurgeCustomerUseCase */
    @Bean public PurgeCustomerUseCase purgeCustomerUseCase(ICustomerRepository r) { return new PurgeCustomerUseCase(r); }

    // ===== Handover & Import =====

    /** @return HandoverBulkCustomerUseCase */
    @Bean public HandoverBulkCustomerUseCase handoverBulkCustomerUseCase(ICustomerRepository r) { return new HandoverBulkCustomerUseCase(r); }
    /** @return ImportBulkCustomerUseCase */
    @Bean public ImportBulkCustomerUseCase importBulkCustomerUseCase(ICustomerRepository r) { return new ImportBulkCustomerUseCase(r); }
}
