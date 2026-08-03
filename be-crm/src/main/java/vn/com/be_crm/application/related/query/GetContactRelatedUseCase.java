package vn.com.be_crm.application.related.query;

import vn.com.be_crm.application.related.dto.ContactRelatedResult;
import vn.com.be_crm.domain.contact.entity.Contact;
import vn.com.be_crm.domain.contact.repository.IContactRepository;
import vn.com.be_crm.domain.related.repository.IRelatedRepository;
import vn.com.be_crm.core.error.frontend.ForbiddenException;
import vn.com.be_crm.core.error.frontend.NotFoundException;

/**
 * Lấy bản ghi liên quan của một liên hệ cho trang chi tiết 360°.
 * Liên hệ không có ownerId — kiểm quyền theo assignedUserId (người phụ trách).
 */
public class GetContactRelatedUseCase {

    private final IContactRepository contactRepo;
    private final IRelatedRepository relatedRepo;

    /** @param contactRepo port liên hệ @param relatedRepo port bản ghi liên quan */
    public GetContactRelatedUseCase(IContactRepository contactRepo, IRelatedRepository relatedRepo) {
        this.contactRepo = contactRepo;
        this.relatedRepo = relatedRepo;
    }

    /**
     * @param contactId  ID liên hệ
     * @param userId     ID người đang đăng nhập
     * @param privileged true nếu ADMIN/SALES_MANAGER (xem mọi liên hệ)
     * @return các nhóm bản ghi liên quan
     */
    public ContactRelatedResult execute(Long contactId, Long userId, boolean privileged) {
        Contact c = contactRepo.findById(contactId)
                .orElseThrow(() -> new NotFoundException("Contact", contactId));
        if (!privileged && (c.getAssignedUserId() == null || !c.getAssignedUserId().equals(userId))) {
            throw new ForbiddenException("Bạn không có quyền xem liên hệ này");
        }
        return relatedRepo.getContactRelated(contactId);
    }
}
