import axiosInstance from '@/core/axios/axiosInstance';
import type { ApiResponse } from '@/shared/types/api';
import type { AdminDashboard, SalesDashboard, DashboardPeriod, RankedItem } from '../types/dashboardTypes';

/** Service gọi API thống kê Dashboard theo vai trò. */
export const dashboardService = {
    getAdmin: (period: DashboardPeriod) =>
        axiosInstance.get<ApiResponse<AdminDashboard>>('/api/dashboard/admin', { params: { period } }),
    getManager: (period: DashboardPeriod) =>
        axiosInstance.get<ApiResponse<SalesDashboard>>('/api/dashboard/manager', { params: { period } }),
    getSale: (period: DashboardPeriod) =>
        axiosInstance.get<ApiResponse<SalesDashboard>>('/api/dashboard/sale', { params: { period } }),
    /** Doanh thu theo chiến dịch trong kỳ (trang phân tích so sánh) — BE tự lọc theo quyền. */
    getRevenueByCampaign: (period: DashboardPeriod) =>
        axiosInstance.get<ApiResponse<RankedItem[]>>('/api/dashboard/revenue-by-campaign', { params: { period } }),
};
