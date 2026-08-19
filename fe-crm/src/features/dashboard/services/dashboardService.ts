import axiosInstance from '@/core/axios/axiosInstance';
import type { ApiResponse } from '@/shared/types/api';
import type {
    AdminDashboard, SalesDashboard, DashboardPeriod, RankedItem, TimeSeriesPoint, DonutSegment,
    KpiMetric, GroupedStatusRow, CampaignCacRow, CountedRankedList, EmployeeWinRateRow,
    CampaignRevenueComparisonRow,
} from '../types/dashboardTypes';

/** Service gọi API thống kê Dashboard theo vai trò + các widget phân tích marketing/hiệu suất ở /phan-tich. */
export const dashboardService = {
    getAdmin: (period: DashboardPeriod) =>
        axiosInstance.get<ApiResponse<AdminDashboard>>('/api/dashboard/admin', { params: { period } }),
    // Mồ côi — chỉ useManagerDashboard.ts (đã comment) gọi tới; endpoint /api/dashboard/manager
    // cũng đã tắt ở BE (DashboardController.manager()). Mở lại cả 3 cùng lúc khi cần dùng lại.
    // getManager: (period: DashboardPeriod) =>
    //     axiosInstance.get<ApiResponse<SalesDashboard>>('/api/dashboard/manager', { params: { period } }),
    getSale: (period: DashboardPeriod) =>
        axiosInstance.get<ApiResponse<SalesDashboard>>('/api/dashboard/sale', { params: { period } }),
    /** Doanh thu theo chiến dịch trong kỳ (trang phân tích so sánh) — BE tự lọc theo quyền. */
    getRevenueByCampaign: (period: DashboardPeriod) =>
        axiosInstance.get<ApiResponse<RankedItem[]>>('/api/dashboard/revenue-by-campaign', { params: { period } }),
    /** Top chiến dịch theo ROI trong kỳ — chỉ ADMIN/SALES_MANAGER. */
    getCampaignRoi: (period: DashboardPeriod) =>
        axiosInstance.get<ApiResponse<RankedItem[]>>('/api/dashboard/campaign-roi', { params: { period } }),
    /** Chi phí trên mỗi lead/cơ hội/đơn hàng theo từng chiến dịch — chỉ ADMIN/SALES_MANAGER. */
    getCampaignCac: (period: DashboardPeriod) =>
        axiosInstance.get<ApiResponse<CampaignCacRow[]>>('/api/dashboard/campaign-cac', { params: { period } }),
    /** Tỉ lệ thắng-thua theo thời gian (12 tháng) — toàn đội hoặc cá nhân tùy quyền. */
    getWinRateTrend: (period: DashboardPeriod) =>
        axiosInstance.get<ApiResponse<TimeSeriesPoint[]>>('/api/dashboard/win-rate-trend', { params: { period } }),
    /** Cơ hội "treo" — toàn đội hoặc cá nhân tùy quyền. */
    getStalledOpportunities: (days?: number) =>
        axiosInstance.get<ApiResponse<CountedRankedList>>('/api/dashboard/stalled-opportunities', { params: { days } }),
    /** Tỉ lệ cơ hội có nguồn từ chiến dịch vs tự phát sinh — chỉ ADMIN/SALES_MANAGER. */
    getOpportunitySource: (period: DashboardPeriod) =>
        axiosInstance.get<ApiResponse<DonutSegment[]>>('/api/dashboard/opportunity-source', { params: { period } }),
    /** Lead đang ở pool chung, chưa ai nhận — chỉ ADMIN/SALES_MANAGER. */
    getLeadPool: () =>
        axiosInstance.get<ApiResponse<CountedRankedList>>('/api/dashboard/lead-pool'),
    /** % doanh thu quy kết được về marketing trên tổng doanh thu — chỉ ADMIN/SALES_MANAGER. */
    getCampaignAttributedRevenue: (period: DashboardPeriod) =>
        axiosInstance.get<ApiResponse<KpiMetric>>('/api/dashboard/campaign-attributed-revenue', { params: { period } }),
    /** So sánh doanh thu kỳ này/kỳ trước theo chiến dịch đang chạy — chỉ ADMIN/SALES_MANAGER. */
    getCampaignPeriodComparison: (period: DashboardPeriod) =>
        axiosInstance.get<ApiResponse<CampaignRevenueComparisonRow[]>>('/api/dashboard/campaign-period-comparison', { params: { period } }),
    /** Bảng xếp hạng nhân viên theo tỉ lệ thắng (kèm doanh thu) — chỉ ADMIN/SALES_MANAGER. */
    getWinRateLeaderboard: (period: DashboardPeriod) =>
        axiosInstance.get<ApiResponse<EmployeeWinRateRow[]>>('/api/dashboard/win-rate-leaderboard', { params: { period } }),
    /** Lý do thua theo từng nhân viên — toàn đội (nhiều nhóm) hoặc cá nhân (1 nhóm) tùy quyền. */
    getLossReasonsByOwner: (period: DashboardPeriod) =>
        axiosInstance.get<ApiResponse<GroupedStatusRow[]>>('/api/dashboard/loss-reasons-by-owner', { params: { period } }),
    /** Số cơ hội "treo" theo từng nhân viên — chỉ ADMIN/SALES_MANAGER. */
    getStalledByOwner: (days?: number) =>
        axiosInstance.get<ApiResponse<RankedItem[]>>('/api/dashboard/stalled-by-owner', { params: { days } }),
};
