package vn.com.be_crm.application.contact.query;

import vn.com.be_crm.application.contact.dto.ContactResult;
import vn.com.be_crm.application.contact.mapper.ContactCommandMapper;
import vn.com.be_crm.core.lookup.port.INameResolver;
import vn.com.be_crm.core.lookup.NameEnricher;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.contact.repository.IContactRepository;
import vn.com.be_crm.core.error.frontend.NotFoundException;

import java.util.List;

/** Use case lấy liên hệ theo ID — kèm tên khóa ngoại để trang chi tiết hiển thị trực tiếp. */
public class GetContactUseCase implements IUseCase<Long, ContactResult> {
    private final IContactRepository repo;
    private final INameResolver names;

    /** @param repo port lưu trữ @param names port tra tên khóa ngoại */
    public GetContactUseCase(IContactRepository repo, INameResolver names) {
        this.repo = repo;
        this.names = names;
    }

    /**
     * Lấy Contact theo ID.
     * @param id ID @return ContactResult (đã điền tên khóa ngoại)
     * @throws NotFoundException nếu không tìm thấy
     */
    @Override
    public ContactResult execute(Long id) {
        ContactResult result = ContactCommandMapper.toResult(
                repo.findById(id).orElseThrow(() -> new NotFoundException("Contact not found: " + id)));
        List<ContactResult> one = List.of(result);
        NameEnricher.apply(one, ContactResult::getCustomerId, names::customers, ContactResult::setCustomerName);
        NameEnricher.apply(one, ContactResult::getAssignedUserId, names::users, ContactResult::setAssignedUserName);
        NameEnricher.apply(one, ContactResult::getCreatedBy, names::users, ContactResult::setCreatedByName);
        NameEnricher.apply(one, ContactResult::getUpdatedBy, names::users, ContactResult::setUpdatedByName);
        return result;
    }
}
