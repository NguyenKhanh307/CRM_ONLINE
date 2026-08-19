import { useLiveQuery } from '@/core/data/useLiveQuery';
import { dashboardService } from '@/features/dashboard/services/dashboardService';
import type { DashboardPeriod } from '@/features/dashboard/types/dashboardTypes';

// % doanh thu quy kết được về marketing trên tổng doanh thu, kỳ này vs kỳ trước — chỉ ADMIN/SALES_MANAGER
export function useCampaignAttributedRevenue(period: DashboardPeriod, enabled = true) {
    return useLiveQuery(
        `dashboard-campaign-attributed-revenue:${period}`,
        () => dashboardService.getCampaignAttributedRevenue(period).then((r) => r.data.data),
        enabled,
    );
}
