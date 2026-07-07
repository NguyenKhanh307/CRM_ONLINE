import { useQuery } from '@tanstack/react-query';
import { dashboardService } from '../services/dashboardService';
import type { DashboardPeriod } from '../types/dashboardTypes';

/**
 * Lấy dữ liệu Dashboard ADMIN theo kỳ.
 * @param period kỳ thống kê
 * @param enabled bật/tắt query (chỉ gọi khi user là ADMIN)
 */
export function useAdminDashboard(period: DashboardPeriod, enabled: boolean) {
    return useQuery({
        queryKey: ['dashboard-admin', period],
        queryFn: () => dashboardService.getAdmin(period).then(r => r.data.data),
        enabled,
    });
}
