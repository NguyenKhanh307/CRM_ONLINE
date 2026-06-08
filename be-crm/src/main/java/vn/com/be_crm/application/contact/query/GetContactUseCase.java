package vn.com.be_crm.application.contact.query;

import vn.com.be_crm.application.contact.dto.ContactResult;
import vn.com.be_crm.application.contact.mapper.ContactCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.contact.repository.IContactRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case lấy liên hệ theo ID. */
public class GetContactUseCase implements IUseCase<Long, ContactResult> {
    private final IContactRepository repo;
    /** @param repo port lưu trữ */
    public GetContactUseCase(IContactRepository repo) { this.repo = repo; }

    /**
     * Lấy Contact theo ID.
     * @param id ID @return ContactResult
     * @throws NotFoundException nếu không tìm thấy
     */
    @Override
    public ContactResult execute(Long id) {
        return ContactCommandMapper.toResult(
                repo.findById(id).orElseThrow(() -> new NotFoundException("Contact not found: " + id)));
    }
}
