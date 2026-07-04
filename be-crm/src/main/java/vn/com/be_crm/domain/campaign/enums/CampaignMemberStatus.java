package vn.com.be_crm.domain.campaign.enums;

/**
 * Trạng thái tương tác của thành viên chiến dịch (theo dõi gửi/mở/click/phản hồi).
 */
public enum CampaignMemberStatus {
    pending, sent, opened, clicked, bounced, responded, unsubscribed
}
