package vn.com.be_crm.application.quotation.command;

import vn.com.be_crm.application.quotation.dto.QuotationResult;
import vn.com.be_crm.application.quotation.mapper.QuotationCommandMapper;
import vn.com.be_crm.domain.quotation.entity.Quotation;
import vn.com.be_crm.domain.quotation.repository.IQuotationRepository;
import vn.com.be_crm.core.error.frontend.DomainException;
import vn.com.be_crm.core.error.frontend.NotFoundException;

/**
 * Use case đặt một báo giá làm báo giá đồng bộ (primary) của cơ hội —
 * đảm bảo chỉ duy nhất một báo giá primary trên mỗi cơ hội.
 */
public class SetPrimaryQuotationUseCase {
    private final IQuotationRepository repo;

    /** @param repo port lưu trữ báo giá */
    public SetPrimaryQuotationUseCase(IQuotationRepository repo) { this.repo = repo; }

    /**
     * Đặt báo giá làm primary; gỡ cờ primary ở các báo giá khác cùng cơ hội.
     * @param quotationId ID báo giá @return báo giá sau cập nhật
     */
    public QuotationResult execute(Long quotationId) {
        Quotation q = repo.findById(quotationId)
                .orElseThrow(() -> new NotFoundException("Quotation not found: " + quotationId));
        if (q.getOpportunityId() == null) {
            throw new DomainException("Báo giá chưa gắn cơ hội nên không thể đặt làm báo giá đồng bộ");
        }
        // Gỡ primary ở các báo giá khác cùng cơ hội
        for (Quotation other : repo.findAllByOpportunityId(q.getOpportunityId())) {
            if (!other.getId().equals(quotationId) && other.isPrimary()) {
                repo.save(other.toBuilder().isPrimary(false).build());
            }
        }
        return QuotationCommandMapper.toResult(repo.save(q.toBuilder().isPrimary(true).build()));
    }
}
