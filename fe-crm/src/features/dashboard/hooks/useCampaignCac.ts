import { useLiveQuery } from '@/core/data/useLiveQuery';
import { dashboardService } from '@/features/dashboard/services/dashboardService';
import type { DashboardPeriod } from '@/features/dashboard/types/dashboardTypes';

// chi phí trên mỗi lead/cơ hội/đơn hàng theo từng chiến dịch — chỉ ADMIN/SALES_MANAGER
export function useCampaignCac(period: DashboardPeriod, enabled = true) {
    return useLiveQuery(`dashboard-campaign-cac:${period}`, () => dashboardService.getCampaignCac(period).then((r) => r.data.data), enabled);
}
