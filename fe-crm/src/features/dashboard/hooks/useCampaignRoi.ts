import { useLiveQuery } from '@/core/data/useLiveQuery';
import { dashboardService } from '@/features/dashboard/services/dashboardService';
import type { DashboardPeriod } from '@/features/dashboard/types/dashboardTypes';

// top chiến dịch theo ROI trong kỳ — chỉ ADMIN/SALES_MANAGER gọi (BE trả 403 với sale)
export function useCampaignRoi(period: DashboardPeriod, enabled = true) {
    return useLiveQuery(`dashboard-campaign-roi:${period}`, () => dashboardService.getCampaignRoi(period).then((r) => r.data.data), enabled);
}
