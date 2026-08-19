import { useLiveQuery } from '@/core/data/useLiveQuery';
import { dashboardService } from '@/features/dashboard/services/dashboardService';
import type { DashboardPeriod } from '@/features/dashboard/types/dashboardTypes';

// lý do thua theo từng nhân viên trong kỳ — BE tự giới hạn theo quyền (nhiều nhóm hoặc 1 nhóm)
export function useLossReasonsByOwner(period: DashboardPeriod) {
    return useLiveQuery(`dashboard-loss-reasons-by-owner:${period}`, () => dashboardService.getLossReasonsByOwner(period).then((r) => r.data.data));
}
