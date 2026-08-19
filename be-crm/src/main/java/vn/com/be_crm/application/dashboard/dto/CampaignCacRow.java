package vn.com.be_crm.application.dashboard.dto;

import java.math.BigDecimal;

/**
 * Chi phí trên mỗi lead/cơ hội/đơn hàng (CAC) của một chiến dịch trong kỳ.
 *
 * @param campaignId         ID chiến dịch
 * @param name                tên chiến dịch
 * @param actualCost          chi phí thực tế đã chi
 * @param leadCount           số lead gắn chiến dịch
 * @param opportunityCount    số cơ hội gắn chiến dịch
 * @param orderCount          số đơn hàng gắn chiến dịch (qua chuỗi order→quotation→opportunity)
 * @param costPerLead         actualCost / leadCount (null nếu leadCount = 0)
 * @param costPerOpportunity  actualCost / opportunityCount (null nếu opportunityCount = 0)
 * @param costPerOrder        actualCost / orderCount (null nếu orderCount = 0)
 */
public record CampaignCacRow(
        Long campaignId,
        String name,
        BigDecimal actualCost,
        long leadCount,
        long opportunityCount,
        long orderCount,
        BigDecimal costPerLead,
        BigDecimal costPerOpportunity,
        BigDecimal costPerOrder
) {
}
