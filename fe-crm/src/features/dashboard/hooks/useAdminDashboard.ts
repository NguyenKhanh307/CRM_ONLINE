import { useLiveQuery } from '@/core/data/useLiveQuery';
import { dashboardService } from '../services/dashboardService';
import type { DashboardPeriod } from '../types/dashboardTypes';

/**
 * Lấy dữ liệu Dashboard ADMIN theo kỳ.
 * @param period kỳ thống kê
 * @param enabled bật/tắt truy vấn (chỉ gọi khi user là ADMIN)
 */
export function useAdminDashboard(period: DashboardPeriod, enabled: boolean) {
    return useLiveQuery(`dashboard-admin:${period}`, () => dashboardService.getAdmin(period).then(r => r.data.data), enabled);
}
