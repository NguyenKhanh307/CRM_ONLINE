package vn.com.be_crm.application.dashboard.dto;

/**
 * So sánh doanh thu kỳ này vs kỳ trước của một chiến dịch đang chạy.
 *
 * @param campaignId ID chiến dịch
 * @param name       tên chiến dịch
 * @param revenue    doanh thu kỳ hiện tại/kỳ trước kèm % tăng trưởng
 */
public record CampaignRevenueComparisonRow(Long campaignId, String name, KpiMetric revenue) {
}
