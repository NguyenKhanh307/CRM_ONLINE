import { useLiveQuery } from '@/core/data/useLiveQuery';
import { dashboardService } from '@/features/dashboard/services/dashboardService';
import type { DashboardPeriod } from '@/features/dashboard/types/dashboardTypes';

// tỉ lệ thắng-thua theo tháng (12 tháng gần nhất) — BE tự giới hạn theo quyền (toàn đội hoặc cá nhân)
export function useWinRateTrend(period: DashboardPeriod) {
    return useLiveQuery(`dashboard-win-rate-trend:${period}`, () => dashboardService.getWinRateTrend(period).then((r) => r.data.data));
}
