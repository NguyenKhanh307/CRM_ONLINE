package vn.com.be_crm.application.contact.command;

import vn.com.be_crm.application.contact.dto.ContactPhoneResult;
import vn.com.be_crm.application.contact.dto.CreateContactPhoneCommand;
import vn.com.be_crm.application.contact.mapper.ContactPhoneCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.contact.repository.IContactPhoneRepository;

/** Use case tạo mới số điện thoại liên hệ. */
public class CreateContactPhoneUseCase implements IUseCase<CreateContactPhoneCommand, ContactPhoneResult> {
    private final IContactPhoneRepository repo;
    /** @param repo port lưu trữ */
    public CreateContactPhoneUseCase(IContactPhoneRepository repo) { this.repo = repo; }

    /**
     * Tạo mới ContactPhone và trả về result.
     * @param cmd dữ liệu tạo mới @return ContactPhoneResult
     */
    @Override
    public ContactPhoneResult execute(CreateContactPhoneCommand cmd) {
        return ContactPhoneCommandMapper.toResult(repo.save(ContactPhoneCommandMapper.toEntity(cmd)));
    }
}
