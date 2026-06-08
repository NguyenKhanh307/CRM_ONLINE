package vn.com.be_crm.application.contact.command;

import vn.com.be_crm.application.contact.dto.ContactResult;
import vn.com.be_crm.application.contact.dto.UpdateContactCommand;
import vn.com.be_crm.application.contact.mapper.ContactCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.contact.entity.Contact;
import vn.com.be_crm.domain.contact.repository.IContactRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case cập nhật liên hệ. */
public class UpdateContactUseCase implements IUseCase<UpdateContactCommand, ContactResult> {
    private final IContactRepository repo;
    /** @param repo port lưu trữ */
    public UpdateContactUseCase(IContactRepository repo) { this.repo = repo; }

    /**
     * Cập nhật Contact và trả về result.
     * @param cmd dữ liệu cập nhật @return ContactResult
     * @throws NotFoundException nếu không tìm thấy
     */
    @Override
    public ContactResult execute(UpdateContactCommand cmd) {
        Contact existing = repo.findById(cmd.getId())
                .orElseThrow(() -> new NotFoundException("Contact not found: " + cmd.getId()));
        return ContactCommandMapper.toResult(repo.save(ContactCommandMapper.toEntity(cmd, existing)));
    }
}
