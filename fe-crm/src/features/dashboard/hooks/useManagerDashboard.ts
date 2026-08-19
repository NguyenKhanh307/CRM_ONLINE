// Mồ côi từ khi ManagerDashboardView chuyển sang 11 hook riêng (campaign ROI/CAC, win-rate...),
// không còn nơi nào import hook này. Mở lại: bỏ comment thân hook + getManager() trong
// dashboardService.ts + method manager() trong DashboardController.java (BE).
// import { useLiveQuery } from '@/core/data/useLiveQuery';
// import { dashboardService } from '../services/dashboardService';
// import type { DashboardPeriod } from '../types/dashboardTypes';
//
// /**
//  * Lấy dữ liệu Dashboard kinh doanh toàn đội (SALES_MANAGER) theo kỳ.
//  * @param period kỳ thống kê
//  * @param enabled bật/tắt truy vấn (chỉ gọi khi user là ADMIN/SALES_MANAGER)
//  */
// export function useManagerDashboard(period: DashboardPeriod, enabled: boolean) {
//     return useLiveQuery(`dashboard-manager:${period}`, () => dashboardService.getManager(period).then(r => r.data.data), enabled);
// }
