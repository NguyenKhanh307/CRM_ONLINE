package vn.com.be_crm.application.related.dto;

/**
 * Bản ghi liên quan của một báo giá — dữ liệu cho trang chi tiết 360° Báo giá.
 *
 * @param orders     đơn hàng phát sinh từ báo giá
 * @param invoices   hóa đơn phát sinh từ báo giá
 * @param activities dòng thời gian hoạt động gắn với báo giá
 */
public record QuotationRelatedResult(
        RelatedGroup orders,
        RelatedGroup invoices,
        RelatedGroup activities) {
}
