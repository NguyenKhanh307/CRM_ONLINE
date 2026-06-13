package vn.com.be_crm.application.contact.command;

import vn.com.be_crm.application.contact.dto.ContactResult;
import vn.com.be_crm.application.contact.dto.CreateContactCommand;
import vn.com.be_crm.application.contact.mapper.ContactCommandMapper;
import vn.com.be_crm.application.contact.mapper.ContactPhoneCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.contact.repository.IContactRepository;

import java.util.stream.Collectors;

/** Use case tạo mới liên hệ (kèm danh sách số điện thoại nếu có). */
public class CreateContactUseCase implements IUseCase<CreateContactCommand, ContactResult> {
    private final IContactRepository repo;
    /** @param repo port lưu trữ */
    public CreateContactUseCase(IContactRepository repo) { this.repo = repo; }

    /**
     * Tạo mới Contact; nếu có phones thì lưu liên hệ + số điện thoại trong một transaction.
     * @param cmd dữ liệu tạo mới @return ContactResult
     */
    @Override
    public ContactResult execute(CreateContactCommand cmd) {
        var entity = ContactCommandMapper.toEntity(cmd);
        if (cmd.getPhones() != null && !cmd.getPhones().isEmpty()) {
            var phones = cmd.getPhones().stream()
                    .map(ContactPhoneCommandMapper::toEntity).collect(Collectors.toList());
            return ContactCommandMapper.toResult(repo.saveWithPhones(entity, phones));
        }
        return ContactCommandMapper.toResult(repo.save(entity));
    }
}
