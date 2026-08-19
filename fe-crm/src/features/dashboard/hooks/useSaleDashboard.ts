import { useLiveQuery } from '@/core/data/useLiveQuery';
import { dashboardService } from '../services/dashboardService';
import type { DashboardPeriod } from '../types/dashboardTypes';

/**
 * Lấy dữ liệu Dashboard cá nhân (SALES_STAFF) theo kỳ.
 * @param period kỳ thống kê
 * @param enabled bật/tắt truy vấn
 */
export function useSaleDashboard(period: DashboardPeriod, enabled: boolean) {
    return useLiveQuery(`dashboard-sale:${period}`, () => dashboardService.getSale(period).then(r => r.data.data), enabled);
}
