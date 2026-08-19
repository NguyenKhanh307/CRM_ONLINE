import { useLiveQuery } from '@/core/data/useLiveQuery';
import { dashboardService } from '@/features/dashboard/services/dashboardService';
import type { DashboardPeriod } from '@/features/dashboard/types/dashboardTypes';

// bảng xếp hạng nhân viên theo tỉ lệ thắng (kèm doanh thu) trong kỳ — chỉ ADMIN/SALES_MANAGER
// nguồn dữ liệu chọn nhân viên cho khối drill-down (OwnerDrilldown) — tránh gọi thêm GET /api/users
export function useWinRateLeaderboard(period: DashboardPeriod, enabled = true) {
    return useLiveQuery(`dashboard-win-rate-leaderboard:${period}`, () => dashboardService.getWinRateLeaderboard(period).then((r) => r.data.data), enabled);
}
