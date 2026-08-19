package vn.com.be_crm.core.config.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.com.be_crm.application.dashboard.query.GetAdminDashboardUseCase;
import vn.com.be_crm.application.dashboard.query.GetCampaignAttributedRevenueUseCase;
import vn.com.be_crm.application.dashboard.query.GetCampaignCacUseCase;
import vn.com.be_crm.application.dashboard.query.GetCampaignPeriodComparisonUseCase;
import vn.com.be_crm.application.dashboard.query.GetCampaignRoiUseCase;
import vn.com.be_crm.application.dashboard.query.GetLeadPoolUseCase;
import vn.com.be_crm.application.dashboard.query.GetLossReasonsByOwnerUseCase;
import vn.com.be_crm.application.dashboard.query.GetOpportunitySourceUseCase;
import vn.com.be_crm.application.dashboard.query.GetRevenueByCampaignUseCase;
import vn.com.be_crm.application.dashboard.query.GetSalesDashboardUseCase;
import vn.com.be_crm.application.dashboard.query.GetStalledByOwnerUseCase;
import vn.com.be_crm.application.dashboard.query.GetStalledOpportunitiesUseCase;
import vn.com.be_crm.application.dashboard.query.GetWinRateLeaderboardUseCase;
import vn.com.be_crm.application.dashboard.query.GetWinRateTrendUseCase;
import vn.com.be_crm.domain.dashboard.repository.IDashboardRepository;

/**
 * Wire các UseCase của module Dashboard (thống kê admin + kinh doanh) qua @Bean.
 */
@Configuration
public class DashboardBeanConfig {

    /** @param r port thống kê @return GetAdminDashboardUseCase */
    @Bean
    public GetAdminDashboardUseCase getAdminDashboardUseCase(IDashboardRepository r) {
        return new GetAdminDashboardUseCase(r);
    }

    /** @param r port thống kê @return GetSalesDashboardUseCase */
    @Bean
    public GetSalesDashboardUseCase getSalesDashboardUseCase(IDashboardRepository r) {
        return new GetSalesDashboardUseCase(r);
    }

    /** @param r port thống kê @return GetRevenueByCampaignUseCase */
    @Bean
    public GetRevenueByCampaignUseCase getRevenueByCampaignUseCase(IDashboardRepository r) {
        return new GetRevenueByCampaignUseCase(r);
    }

    /** @param r port thống kê @return GetCampaignRoiUseCase */
    @Bean
    public GetCampaignRoiUseCase getCampaignRoiUseCase(IDashboardRepository r) {
        return new GetCampaignRoiUseCase(r);
    }

    /** @param r port thống kê @return GetCampaignCacUseCase */
    @Bean
    public GetCampaignCacUseCase getCampaignCacUseCase(IDashboardRepository r) {
        return new GetCampaignCacUseCase(r);
    }

    /** @param r port thống kê @return GetWinRateTrendUseCase */
    @Bean
    public GetWinRateTrendUseCase getWinRateTrendUseCase(IDashboardRepository r) {
        return new GetWinRateTrendUseCase(r);
    }

    /** @param r port thống kê @return GetStalledOpportunitiesUseCase */
    @Bean
    public GetStalledOpportunitiesUseCase getStalledOpportunitiesUseCase(IDashboardRepository r) {
        return new GetStalledOpportunitiesUseCase(r);
    }

    /** @param r port thống kê @return GetOpportunitySourceUseCase */
    @Bean
    public GetOpportunitySourceUseCase getOpportunitySourceUseCase(IDashboardRepository r) {
        return new GetOpportunitySourceUseCase(r);
    }

    /** @param r port thống kê @return GetLeadPoolUseCase */
    @Bean
    public GetLeadPoolUseCase getLeadPoolUseCase(IDashboardRepository r) {
        return new GetLeadPoolUseCase(r);
    }

    /** @param r port thống kê @return GetCampaignAttributedRevenueUseCase */
    @Bean
    public GetCampaignAttributedRevenueUseCase getCampaignAttributedRevenueUseCase(IDashboardRepository r) {
        return new GetCampaignAttributedRevenueUseCase(r);
    }

    /** @param r port thống kê @return GetCampaignPeriodComparisonUseCase */
    @Bean
    public GetCampaignPeriodComparisonUseCase getCampaignPeriodComparisonUseCase(IDashboardRepository r) {
        return new GetCampaignPeriodComparisonUseCase(r);
    }

    /** @param r port thống kê @return GetWinRateLeaderboardUseCase */
    @Bean
    public GetWinRateLeaderboardUseCase getWinRateLeaderboardUseCase(IDashboardRepository r) {
        return new GetWinRateLeaderboardUseCase(r);
    }

    /** @param r port thống kê @return GetLossReasonsByOwnerUseCase */
    @Bean
    public GetLossReasonsByOwnerUseCase getLossReasonsByOwnerUseCase(IDashboardRepository r) {
        return new GetLossReasonsByOwnerUseCase(r);
    }

    /** @param r port thống kê @return GetStalledByOwnerUseCase */
    @Bean
    public GetStalledByOwnerUseCase getStalledByOwnerUseCase(IDashboardRepository r) {
        return new GetStalledByOwnerUseCase(r);
    }
}
