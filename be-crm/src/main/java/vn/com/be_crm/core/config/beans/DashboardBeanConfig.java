package vn.com.be_crm.core.config.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.com.be_crm.application.dashboard.query.GetAdminDashboardUseCase;
import vn.com.be_crm.application.dashboard.query.GetRevenueByCampaignUseCase;
import vn.com.be_crm.application.dashboard.query.GetSalesDashboardUseCase;
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
}
