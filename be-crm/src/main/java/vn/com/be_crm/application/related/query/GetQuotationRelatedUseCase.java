package vn.com.be_crm.application.related.query;

import vn.com.be_crm.application.related.dto.QuotationRelatedResult;
import vn.com.be_crm.domain.quotation.entity.Quotation;
import vn.com.be_crm.domain.quotation.repository.IQuotationRepository;
import vn.com.be_crm.domain.related.repository.IRelatedRepository;
import vn.com.be_crm.core.error.frontend.ForbiddenException;
import vn.com.be_crm.core.error.frontend.NotFoundException;

/**
 * Lấy bản ghi liên quan của một báo giá cho trang chi tiết 360°.
 * Quyền kiểm tra MỘT LẦN trên báo giá (bản ghi cha); qua được thì trả đủ bản ghi con.
 */
public class GetQuotationRelatedUseCase {

    private final IQuotationRepository quotationRepo;
    private final IRelatedRepository relatedRepo;

    /** @param quotationRepo port báo giá @param relatedRepo port bản ghi liên quan */
    public GetQuotationRelatedUseCase(IQuotationRepository quotationRepo, IRelatedRepository relatedRepo) {
        this.quotationRepo = quotationRepo;
        this.relatedRepo = relatedRepo;
    }

    /**
     * @param quotationId ID báo giá
     * @param userId      ID người đang đăng nhập
     * @param privileged  true nếu ADMIN/SALES_MANAGER (xem mọi báo giá)
     * @return các nhóm bản ghi liên quan
     */
    public QuotationRelatedResult execute(Long quotationId, Long userId, boolean privileged) {
        Quotation q = quotationRepo.findById(quotationId)
                .orElseThrow(() -> new NotFoundException("Quotation", quotationId));
        if (!privileged && (q.getOwnerId() == null || !q.getOwnerId().equals(userId))) {
            throw new ForbiddenException("Bạn không có quyền xem báo giá này");
        }
        return relatedRepo.getQuotationRelated(quotationId);
    }
}
