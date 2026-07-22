package vn.com.be_crm.application.related.dto;

/**
 * Toàn bộ bản ghi quy về một chiến dịch — dữ liệu cho trang chi tiết Chiến dịch.
 * Đây là chiều đọc ngược của attribution: campaign_id được gắn khi tạo/convert ở từng phân hệ.
 *
 * @param leads         tiềm năng sinh từ chiến dịch
 * @param opportunities cơ hội quy về chiến dịch
 * @param orders        đơn hàng quy về chiến dịch
 * @param invoices      hóa đơn quy về chiến dịch
 */
public record CampaignRelatedResult(
        RelatedGroup leads,
        RelatedGroup opportunities,
        RelatedGroup orders,
        RelatedGroup invoices) {
}
