import { useLiveQuery } from '@/core/data/useLiveQuery';
import { dashboardService } from '@/features/dashboard/services/dashboardService';
import type { DashboardPeriod } from '@/features/dashboard/types/dashboardTypes';

// tỉ lệ cơ hội có nguồn từ chiến dịch vs tự phát sinh trong kỳ — chỉ ADMIN/SALES_MANAGER
export function useOpportunitySource(period: DashboardPeriod, enabled = true) {
    return useLiveQuery(`dashboard-opportunity-source:${period}`, () => dashboardService.getOpportunitySource(period).then((r) => r.data.data), enabled);
}
