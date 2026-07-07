import { useQuery } from '@tanstack/react-query';
import { dashboardService } from '../services/dashboardService';
import type { DashboardPeriod } from '../types/dashboardTypes';

/**
 * Lấy dữ liệu Dashboard cá nhân (SALES_STAFF) theo kỳ.
 * @param period kỳ thống kê
 * @param enabled bật/tắt query
 */
export function useSaleDashboard(period: DashboardPeriod, enabled: boolean) {
    return useQuery({
        queryKey: ['dashboard-sale', period],
        queryFn: () => dashboardService.getSale(period).then(r => r.data.data),
        enabled,
    });
}
