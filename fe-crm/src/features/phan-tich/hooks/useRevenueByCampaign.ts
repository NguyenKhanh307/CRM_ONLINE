import { useQuery } from '@tanstack/react-query';
import { dashboardService } from '@/features/dashboard/services/dashboardService';
import type { DashboardPeriod } from '@/features/dashboard/types/dashboardTypes';

/**
 * Doanh thu theo chiến dịch trong kỳ — dữ liệu cho khối "So sánh theo chiến dịch" của trang phân tích.
 * BE tự giới hạn theo quyền (nhân viên chỉ thấy hóa đơn mình phụ trách).
 *
 * @param period mã kỳ (month/quarter/year)
 */
export function useRevenueByCampaign(period: DashboardPeriod) {
    return useQuery({
        queryKey: ['dashboard-revenue-by-campaign', period],
        queryFn: () => dashboardService.getRevenueByCampaign(period).then((r) => r.data.data),
    });
}
