package vn.com.be_crm.application.contact.command;

import vn.com.be_crm.application.contact.dto.ContactPhoneResult;
import vn.com.be_crm.application.contact.dto.UpdateContactPhoneCommand;
import vn.com.be_crm.application.contact.mapper.ContactPhoneCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.contact.entity.ContactPhone;
import vn.com.be_crm.domain.contact.repository.IContactPhoneRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case cập nhật số điện thoại liên hệ. */
public class UpdateContactPhoneUseCase implements IUseCase<UpdateContactPhoneCommand, ContactPhoneResult> {
    private final IContactPhoneRepository repo;
    /** @param repo port lưu trữ */
    public UpdateContactPhoneUseCase(IContactPhoneRepository repo) { this.repo = repo; }

    /**
     * Cập nhật ContactPhone và trả về result.
     * @param cmd dữ liệu cập nhật @return ContactPhoneResult
     * @throws NotFoundException nếu không tìm thấy
     */
    @Override
    public ContactPhoneResult execute(UpdateContactPhoneCommand cmd) {
        ContactPhone existing = repo.findById(cmd.getId())
                .orElseThrow(() -> new NotFoundException("ContactPhone not found: " + cmd.getId()));
        return ContactPhoneCommandMapper.toResult(repo.save(ContactPhoneCommandMapper.toEntity(cmd, existing)));
    }
}
