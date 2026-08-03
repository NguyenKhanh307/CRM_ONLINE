package vn.com.be_crm.application.related.query;

import vn.com.be_crm.application.related.dto.OpportunityRelatedResult;
import vn.com.be_crm.domain.opportunity.entity.Opportunity;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityRepository;
import vn.com.be_crm.domain.related.repository.IRelatedRepository;
import vn.com.be_crm.core.error.frontend.ForbiddenException;
import vn.com.be_crm.core.error.frontend.NotFoundException;

/**
 * Lấy toàn bộ bản ghi liên quan của một cơ hội cho trang chi tiết 360°.
 * Quyền kiểm tra một lần trên cơ hội (bản ghi cha) — xem {@link GetCustomerRelatedUseCase}.
 */
public class GetOpportunityRelatedUseCase {

    private final IOpportunityRepository opportunityRepo;
    private final IRelatedRepository relatedRepo;

    /** @param opportunityRepo port cơ hội @param relatedRepo port bản ghi liên quan */
    public GetOpportunityRelatedUseCase(IOpportunityRepository opportunityRepo, IRelatedRepository relatedRepo) {
        this.opportunityRepo = opportunityRepo;
        this.relatedRepo = relatedRepo;
    }

    /**
     * @param opportunityId ID cơ hội
     * @param userId        ID người đang đăng nhập
     * @param privileged    true nếu ADMIN/SALES_MANAGER (xem mọi cơ hội)
     * @return các nhóm bản ghi liên quan
     */
    public OpportunityRelatedResult execute(Long opportunityId, Long userId, boolean privileged) {
        Opportunity o = opportunityRepo.findById(opportunityId)
                .orElseThrow(() -> new NotFoundException("Opportunity", opportunityId));
        if (!privileged && (o.getOwnerId() == null || !o.getOwnerId().equals(userId))) {
            throw new ForbiddenException("Bạn không có quyền xem cơ hội này");
        }
        return relatedRepo.getOpportunityRelated(opportunityId);
    }
}
