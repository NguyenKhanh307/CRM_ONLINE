import { useLiveQuery } from '@/core/data/useLiveQuery';
import { dashboardService } from '@/features/dashboard/services/dashboardService';
import type { DashboardPeriod } from '@/features/dashboard/types/dashboardTypes';

// so sánh doanh thu kỳ này/kỳ trước cho các chiến dịch đang chạy song song — chỉ ADMIN/SALES_MANAGER
export function useCampaignPeriodComparison(period: DashboardPeriod, enabled = true) {
    return useLiveQuery(
        `dashboard-campaign-period-comparison:${period}`,
        () => dashboardService.getCampaignPeriodComparison(period).then((r) => r.data.data),
        enabled,
    );
}
