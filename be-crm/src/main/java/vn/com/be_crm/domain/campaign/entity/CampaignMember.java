package vn.com.be_crm.domain.campaign.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.campaign.enums.CampaignMemberStatus;

import java.time.LocalDateTime;

/**
 * Domain entity đại diện cho thành viên (người nhận) trong chiến dịch marketing.
 */
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CampaignMember {
    /** ID thành viên. */
    private Long id;
    /** ID chiến dịch. */
    private Long campaignId;
    /** ID tiềm năng (nếu thành viên là tiềm năng). */
    private Long leadId;
    /** ID liên hệ (nếu thành viên là liên hệ). */
    private Long contactId;
    /** Tên (nhập tay/import). */
    private String name;
    /** Email. */
    private String email;
    /** Số điện thoại. */
    private String phone;
    /** Trạng thái tương tác. */
    private CampaignMemberStatus status;
    /** Thời điểm gửi email. */
    private LocalDateTime sentAt;
    /** Thời điểm phản hồi. */
    private LocalDateTime respondedAt;
    /** Thời điểm tạo. */
    private LocalDateTime createdAt;
    /** Thời điểm cập nhật gần nhất. */
    private LocalDateTime updatedAt;
}
