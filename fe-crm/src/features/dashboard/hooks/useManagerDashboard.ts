import { useQuery } from '@tanstack/react-query';
import { dashboardService } from '../services/dashboardService';
import type { DashboardPeriod } from '../types/dashboardTypes';

/**
 * Lấy dữ liệu Dashboard kinh doanh toàn đội (SALES_MANAGER) theo kỳ.
 * @param period kỳ thống kê
 * @param enabled bật/tắt query (chỉ gọi khi user là ADMIN/SALES_MANAGER)
 */
export function useManagerDashboard(period: DashboardPeriod, enabled: boolean) {
    return useQuery({
        queryKey: ['dashboard-manager', period],
        queryFn: () => dashboardService.getManager(period).then(r => r.data.data),
        enabled,
    });
}
