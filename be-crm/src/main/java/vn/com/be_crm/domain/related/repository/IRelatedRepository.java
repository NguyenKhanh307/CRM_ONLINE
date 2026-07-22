package vn.com.be_crm.domain.related.repository;

import vn.com.be_crm.application.related.dto.CampaignRelatedResult;
import vn.com.be_crm.application.related.dto.ContactRelatedResult;
import vn.com.be_crm.application.related.dto.CustomerRelatedResult;
import vn.com.be_crm.application.related.dto.InvoiceRelatedResult;
import vn.com.be_crm.application.related.dto.LeadRelatedResult;
import vn.com.be_crm.application.related.dto.OpportunityRelatedResult;
import vn.com.be_crm.application.related.dto.OrderRelatedResult;
import vn.com.be_crm.application.related.dto.QuotationRelatedResult;

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

    /**
     * Lấy bản ghi liên quan của một tiềm năng (cơ hội đã convert + hoạt động).
     *
     * @param leadId ID tiềm năng
     * @return các nhóm bản ghi liên quan kèm tổng số
     */
    LeadRelatedResult getLeadRelated(Long leadId);

    /**
     * Lấy bản ghi liên quan của một liên hệ (cơ hội/báo giá/đơn/hóa đơn/phiếu CS/hoạt động theo contact_id).
     *
     * @param contactId ID liên hệ
     * @return các nhóm bản ghi liên quan kèm tổng số
     */
    ContactRelatedResult getContactRelated(Long contactId);

    /**
     * Lấy bản ghi liên quan của một báo giá (đơn hàng/hóa đơn phát sinh + hoạt động).
     *
     * @param quotationId ID báo giá
     * @return các nhóm bản ghi liên quan kèm tổng số
     */
    QuotationRelatedResult getQuotationRelated(Long quotationId);

    /**
     * Lấy bản ghi liên quan của một đơn hàng (hóa đơn + hoạt động).
     *
     * @param orderId ID đơn hàng
     * @return các nhóm bản ghi liên quan kèm tổng số
     */
    OrderRelatedResult getOrderRelated(Long orderId);

    /**
     * Lấy bản ghi liên quan của một hóa đơn (phiếu chăm sóc + hoạt động).
     *
     * @param invoiceId ID hóa đơn
     * @return các nhóm bản ghi liên quan kèm tổng số
     */
    InvoiceRelatedResult getInvoiceRelated(Long invoiceId);

    /**
     * Lấy bản ghi quy về một chiến dịch (tiềm năng/cơ hội/đơn hàng/hóa đơn có campaign_id trỏ tới).
     *
     * @param campaignId ID chiến dịch
     * @return các nhóm bản ghi liên quan kèm tổng số
     */
    CampaignRelatedResult getCampaignRelated(Long campaignId);
}
