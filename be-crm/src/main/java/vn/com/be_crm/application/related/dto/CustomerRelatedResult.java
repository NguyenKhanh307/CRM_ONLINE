package vn.com.be_crm.application.related.dto;

/**
 * Toàn bộ bản ghi liên quan của một khách hàng — dữ liệu cho trang chi tiết 360° Khách hàng.
 *
 * @param contacts      liên hệ thuộc khách hàng
 * @param opportunities cơ hội của khách hàng
 * @param quotations    báo giá của khách hàng
 * @param orders        đơn hàng của khách hàng
 * @param invoices      hóa đơn của khách hàng
 * @param tickets       phiếu chăm sóc của khách hàng
 * @param activities    dòng thời gian hoạt động gắn với khách hàng
 */
public record CustomerRelatedResult(
        RelatedGroup contacts,
        RelatedGroup opportunities,
        RelatedGroup quotations,
        RelatedGroup orders,
        RelatedGroup invoices,
        RelatedGroup tickets,
        RelatedGroup activities) {
}
