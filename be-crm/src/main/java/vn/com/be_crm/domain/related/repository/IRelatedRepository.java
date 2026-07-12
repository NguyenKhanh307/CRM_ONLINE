package vn.com.be_crm.domain.related.repository;

import vn.com.be_crm.application.related.dto.CustomerRelatedResult;
import vn.com.be_crm.application.related.dto.OpportunityRelatedResult;

/**
 * Port đọc bản ghi liên quan cho trang chi tiết 360° (chỉ đọc, native SQL — giống IDashboardRepository).
 * Cố ý KHÔNG lọc theo owner: quyền đã được kiểm tra một lần trên bản ghi cha ở use case,
 * qua được thì phải thấy đủ bản ghi con (kể cả của đồng nghiệp) — nếu không sale sẽ báo giá trùng.
 */
public interface IRelatedRepository {

    /**
     * Lấy toàn bộ bản ghi liên quan của một khách hàng.
     *
     * @param customerId ID khách hàng
     * @return các nhóm bản ghi liên quan kèm tổng số
     */
    CustomerRelatedResult getCustomerRelated(Long customerId);

    /**
     * Lấy toàn bộ bản ghi liên quan của một cơ hội.
     *
     * @param opportunityId ID cơ hội
     * @return các nhóm bản ghi liên quan kèm tổng số
     */
    OpportunityRelatedResult getOpportunityRelated(Long opportunityId);
}
