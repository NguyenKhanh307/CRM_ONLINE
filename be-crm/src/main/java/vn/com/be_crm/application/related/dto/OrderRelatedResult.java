package vn.com.be_crm.application.related.dto;

/**
 * Bản ghi liên quan của một đơn hàng — dữ liệu cho trang chi tiết 360° Đơn hàng.
 *
 * @param invoices   hóa đơn xuất từ đơn hàng (0 hoặc 1)
 * @param activities dòng thời gian hoạt động gắn với đơn hàng
 */
public record OrderRelatedResult(
        RelatedGroup invoices,
        RelatedGroup activities) {
}
