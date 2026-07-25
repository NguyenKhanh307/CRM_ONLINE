package vn.com.be_crm.application.campaign.dto;

/**
 * Chiến dịch ở dạng rút gọn cho landing page công khai (web tracking).
 *
 * <p>Cố ý chỉ gồm 3 trường đủ để gắn nguồn cho tiềm năng — <b>không</b> kèm ngân sách,
 * chi phí, doanh số kỳ vọng hay người phụ trách, vì endpoint phục vụ khách ẩn danh.</p>
 *
 * @param id   ID chiến dịch (gắn vào lead)
 * @param code Mã chiến dịch — dùng làm giá trị `utm_campaign` trên URL landing page
 * @param name Tên chiến dịch hiển thị cho người chọn
 */
public record PublicCampaignResult(Long id, String code, String name) {
}
