package vn.com.be_crm.application.contact.command;

import vn.com.be_crm.application.contact.dto.ContactResult;
import vn.com.be_crm.application.contact.dto.CreateContactCommand;
import vn.com.be_crm.application.contact.mapper.ContactCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.contact.repository.IContactRepository;

/** Use case tạo mới liên hệ. */
public class CreateContactUseCase implements IUseCase<CreateContactCommand, ContactResult> {
    private final IContactRepository repo;
    /** @param repo port lưu trữ */
    public CreateContactUseCase(IContactRepository repo) { this.repo = repo; }

    /**
     * Tạo mới Contact và trả về result.
     * @param cmd dữ liệu tạo mới @return ContactResult
     */
    @Override
    public ContactResult execute(CreateContactCommand cmd) {
        return ContactCommandMapper.toResult(repo.save(ContactCommandMapper.toEntity(cmd)));
    }
}
