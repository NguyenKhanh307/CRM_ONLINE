package vn.com.be_crm.application.campaign.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** Output DTO thống kê hiệu quả (ROI) của chiến dịch. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class CampaignStatsResult {
    /** ID chiến dịch. */
    private Long campaignId;
    /** Số thành viên trong chiến dịch. */
    private long memberCount;
    /** Số email đã gửi. */
    private long sentCount;
    /** Số tiềm năng gắn chiến dịch. */
    private long leadCount;
    /** Số cơ hội gắn chiến dịch. */
    private long opportunityCount;
    /** Số cơ hội thắng. */
    private long wonOpportunityCount;
    /** Số đơn hàng gắn chiến dịch. */
    private long orderCount;
    /** Tổng doanh thu quy về chiến dịch (từ đơn hàng). */
    private BigDecimal revenue;
    /** Chi phí thực tế của chiến dịch. */
    private BigDecimal actualCost;
}
