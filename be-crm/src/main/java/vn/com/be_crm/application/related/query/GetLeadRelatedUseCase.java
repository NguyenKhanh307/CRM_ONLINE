package vn.com.be_crm.application.related.query;

import vn.com.be_crm.application.related.dto.LeadRelatedResult;
import vn.com.be_crm.domain.lead.entity.Lead;
import vn.com.be_crm.domain.lead.repository.ILeadRepository;
import vn.com.be_crm.domain.related.repository.IRelatedRepository;
import vn.com.be_crm.domain.shared.exception.ForbiddenException;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/**
 * Lấy bản ghi liên quan của một tiềm năng cho trang chi tiết 360°.
 * Quyền kiểm tra MỘT LẦN trên tiềm năng (bản ghi cha); qua được thì trả đủ bản ghi con.
 */
public class GetLeadRelatedUseCase {

    private final ILeadRepository leadRepo;
    private final IRelatedRepository relatedRepo;

    /** @param leadRepo port tiềm năng @param relatedRepo port bản ghi liên quan */
    public GetLeadRelatedUseCase(ILeadRepository leadRepo, IRelatedRepository relatedRepo) {
        this.leadRepo = leadRepo;
        this.relatedRepo = relatedRepo;
    }

    /**
     * @param leadId     ID tiềm năng
     * @param userId     ID người đang đăng nhập
     * @param privileged true nếu ADMIN/SALES_MANAGER (xem mọi tiềm năng)
     * @return các nhóm bản ghi liên quan
     */
    public LeadRelatedResult execute(Long leadId, Long userId, boolean privileged) {
        Lead l = leadRepo.findById(leadId)
                .orElseThrow(() -> new NotFoundException("Lead", leadId));
        if (!privileged && (l.getOwnerId() == null || !l.getOwnerId().equals(userId))) {
            throw new ForbiddenException("Bạn không có quyền xem tiềm năng này");
        }
        return relatedRepo.getLeadRelated(leadId);
    }
}
