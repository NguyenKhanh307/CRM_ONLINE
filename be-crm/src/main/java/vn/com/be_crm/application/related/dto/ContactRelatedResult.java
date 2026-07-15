package vn.com.be_crm.application.related.dto;

/**
 * Bản ghi liên quan của một liên hệ — dữ liệu cho trang chi tiết 360° Liên hệ.
 *
 * @param opportunities cơ hội gắn liên hệ
 * @param quotations    báo giá gắn liên hệ
 * @param orders        đơn hàng gắn liên hệ
 * @param invoices      hóa đơn gắn liên hệ
 * @param tickets       phiếu chăm sóc gắn liên hệ
 * @param activities    dòng thời gian hoạt động gắn với liên hệ
 */
public record ContactRelatedResult(
        RelatedGroup opportunities,
        RelatedGroup quotations,
        RelatedGroup orders,
        RelatedGroup invoices,
        RelatedGroup tickets,
        RelatedGroup activities) {
}
