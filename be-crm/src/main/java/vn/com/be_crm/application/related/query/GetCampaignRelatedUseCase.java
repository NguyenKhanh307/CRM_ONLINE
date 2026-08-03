package vn.com.be_crm.application.related.query;

import vn.com.be_crm.application.related.dto.CampaignRelatedResult;
import vn.com.be_crm.domain.campaign.entity.Campaign;
import vn.com.be_crm.domain.campaign.repository.ICampaignRepository;
import vn.com.be_crm.domain.related.repository.IRelatedRepository;
import vn.com.be_crm.core.error.frontend.ForbiddenException;
import vn.com.be_crm.core.error.frontend.NotFoundException;

/**
 * Lấy toàn bộ bản ghi quy về một chiến dịch cho trang chi tiết Chiến dịch.
 * Quyền kiểm tra một lần trên chiến dịch (bản ghi cha) — xem {@link GetCustomerRelatedUseCase}.
 */
public class GetCampaignRelatedUseCase {

    private final ICampaignRepository campaignRepo;
    private final IRelatedRepository relatedRepo;

    /** @param campaignRepo port chiến dịch @param relatedRepo port bản ghi liên quan */
    public GetCampaignRelatedUseCase(ICampaignRepository campaignRepo, IRelatedRepository relatedRepo) {
        this.campaignRepo = campaignRepo;
        this.relatedRepo = relatedRepo;
    }

    /**
     * @param campaignId ID chiến dịch
     * @param userId     ID người đang đăng nhập
     * @param privileged true nếu ADMIN/SALES_MANAGER (xem mọi chiến dịch)
     * @return các nhóm bản ghi quy về chiến dịch
     */
    public CampaignRelatedResult execute(Long campaignId, Long userId, boolean privileged) {
        Campaign c = campaignRepo.findById(campaignId)
                .orElseThrow(() -> new NotFoundException("Campaign", campaignId));
        if (!privileged && (c.getOwnerId() == null || !c.getOwnerId().equals(userId))) {
            throw new ForbiddenException("Bạn không có quyền xem chiến dịch này");
        }
        return relatedRepo.getCampaignRelated(campaignId);
    }
}
