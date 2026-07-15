package vn.com.be_crm.application.related.dto;

/**
 * Bản ghi liên quan của một hóa đơn — dữ liệu cho trang chi tiết 360° Hóa đơn.
 *
 * @param tickets    phiếu chăm sóc gắn hóa đơn (trả hàng/khiếu nại)
 * @param activities dòng thời gian hoạt động gắn với hóa đơn
 */
public record InvoiceRelatedResult(
        RelatedGroup tickets,
        RelatedGroup activities) {
}
