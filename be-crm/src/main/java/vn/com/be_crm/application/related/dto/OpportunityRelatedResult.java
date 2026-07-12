package vn.com.be_crm.application.related.dto;

/**
 * Toàn bộ bản ghi liên quan của một cơ hội — dữ liệu cho trang chi tiết 360° Cơ hội.
 *
 * @param quotations báo giá sinh từ cơ hội
 * @param orders     đơn hàng sinh từ cơ hội
 * @param invoices   hóa đơn sinh từ cơ hội
 * @param activities dòng thời gian hoạt động gắn với cơ hội
 */
public record OpportunityRelatedResult(
        RelatedGroup quotations,
        RelatedGroup orders,
        RelatedGroup invoices,
        RelatedGroup activities) {
}
