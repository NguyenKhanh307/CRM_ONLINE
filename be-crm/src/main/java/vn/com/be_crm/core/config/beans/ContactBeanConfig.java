package vn.com.be_crm.core.config.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.com.be_crm.application.contact.command.*;
import vn.com.be_crm.application.contact.query.*;
import vn.com.be_crm.core.tx.port.ITransactionRunner;
import vn.com.be_crm.domain.contact.repository.IContactRepository;

/**
 * Wire các UseCase của module Contact (contact, trash, import) qua @Bean.
 */
@Configuration
public class ContactBeanConfig {

    // ===== Contact =====

    /** @return CreateContactUseCase */
    @Bean public CreateContactUseCase createContactUseCase(IContactRepository r) { return new CreateContactUseCase(r); }
    /** @return UpdateContactUseCase */
    @Bean public UpdateContactUseCase updateContactUseCase(IContactRepository r) { return new UpdateContactUseCase(r); }
    /** @return DeleteContactUseCase */
    @Bean public DeleteContactUseCase deleteContactUseCase(IContactRepository r) { return new DeleteContactUseCase(r); }
    /** @return GetContactUseCase */
    @Bean public GetContactUseCase getContactUseCase(IContactRepository r, vn.com.be_crm.core.lookup.port.INameResolver n) { return new GetContactUseCase(r, n); }
    /** @return ListContactUseCase */
    @Bean public ListContactUseCase listContactUseCase(IContactRepository r, vn.com.be_crm.core.lookup.port.INameResolver n) { return new ListContactUseCase(r, n); }

    // ===== Trash =====

    /** @return ListDeletedContactsUseCase */
    @Bean public ListDeletedContactsUseCase listDeletedContactsUseCase(IContactRepository r) { return new ListDeletedContactsUseCase(r); }
    /** @return RestoreContactUseCase */
    @Bean public RestoreContactUseCase restoreContactUseCase(IContactRepository r) { return new RestoreContactUseCase(r); }
    /** @return PurgeContactUseCase */
    @Bean public PurgeContactUseCase purgeContactUseCase(IContactRepository r) { return new PurgeContactUseCase(r); }

    // ===== Import =====

    /** @return ImportBulkContactUseCase */
    @Bean public ImportBulkContactUseCase importBulkContactUseCase(IContactRepository r, ITransactionRunner tx) { return new ImportBulkContactUseCase(r, tx); }
}
