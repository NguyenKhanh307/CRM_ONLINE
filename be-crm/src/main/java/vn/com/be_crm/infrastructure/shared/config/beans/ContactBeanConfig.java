package vn.com.be_crm.infrastructure.shared.config.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.com.be_crm.application.contact.command.*;
import vn.com.be_crm.application.contact.query.*;
import vn.com.be_crm.domain.contact.repository.IContactPhoneRepository;
import vn.com.be_crm.domain.contact.repository.IContactRepository;

/**
 * Wire các UseCase của module Contact (contact, phone, trash, import) qua @Bean.
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
    @Bean public GetContactUseCase getContactUseCase(IContactRepository r, vn.com.be_crm.application.shared.lookup.INameResolver n) { return new GetContactUseCase(r, n); }
    /** @return ListContactUseCase */
    @Bean public ListContactUseCase listContactUseCase(IContactRepository r, vn.com.be_crm.application.shared.lookup.INameResolver n) { return new ListContactUseCase(r, n); }

    // ===== Contact Phone =====

    /** @return CreateContactPhoneUseCase */
    @Bean public CreateContactPhoneUseCase createContactPhoneUseCase(IContactPhoneRepository r) { return new CreateContactPhoneUseCase(r); }
    /** @return UpdateContactPhoneUseCase */
    @Bean public UpdateContactPhoneUseCase updateContactPhoneUseCase(IContactPhoneRepository r) { return new UpdateContactPhoneUseCase(r); }
    /** @return DeleteContactPhoneUseCase */
    @Bean public DeleteContactPhoneUseCase deleteContactPhoneUseCase(IContactPhoneRepository r) { return new DeleteContactPhoneUseCase(r); }
    /** @return ListContactPhoneUseCase */
    @Bean public ListContactPhoneUseCase listContactPhoneUseCase(IContactPhoneRepository r) { return new ListContactPhoneUseCase(r); }

    // ===== Trash =====

    /** @return ListDeletedContactsUseCase */
    @Bean public ListDeletedContactsUseCase listDeletedContactsUseCase(IContactRepository r) { return new ListDeletedContactsUseCase(r); }
    /** @return RestoreContactUseCase */
    @Bean public RestoreContactUseCase restoreContactUseCase(IContactRepository r) { return new RestoreContactUseCase(r); }
    /** @return PurgeContactUseCase */
    @Bean public PurgeContactUseCase purgeContactUseCase(IContactRepository r) { return new PurgeContactUseCase(r); }

    // ===== Import =====

    /** @return ImportBulkContactUseCase */
    @Bean public ImportBulkContactUseCase importBulkContactUseCase(IContactRepository r) { return new ImportBulkContactUseCase(r); }
}
