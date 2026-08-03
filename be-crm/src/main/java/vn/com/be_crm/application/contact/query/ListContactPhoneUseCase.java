package vn.com.be_crm.application.contact.query;

import vn.com.be_crm.application.contact.dto.ContactPhoneResult;
import vn.com.be_crm.application.contact.mapper.ContactPhoneCommandMapper;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.contact.repository.IContactPhoneRepository;

import java.util.List;
import java.util.stream.Collectors;

/** Use case lấy danh sách số điện thoại theo contactId. */
public class ListContactPhoneUseCase implements IUseCase<Long, List<ContactPhoneResult>> {
    private final IContactPhoneRepository repo;
    /** @param repo port lưu trữ */
    public ListContactPhoneUseCase(IContactPhoneRepository repo) { this.repo = repo; }

    /**
     * Lấy danh sách ContactPhone theo contactId.
     * @param contactId ID liên hệ @return danh sách result
     */
    @Override
    public List<ContactPhoneResult> execute(Long contactId) {
        return repo.findAllByContactId(contactId).stream()
                .map(ContactPhoneCommandMapper::toResult).collect(Collectors.toList());
    }
}
