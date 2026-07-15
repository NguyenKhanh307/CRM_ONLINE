package vn.com.be_crm.application.related.dto;

/**
 * Bản ghi liên quan của một tiềm năng — dữ liệu cho trang chi tiết 360° Tiềm năng.
 *
 * @param opportunities cơ hội đã convert từ tiềm năng (0 hoặc 1)
 * @param activities    dòng thời gian hoạt động gắn với tiềm năng
 */
public record LeadRelatedResult(
        RelatedGroup opportunities,
        RelatedGroup activities) {
}
