/** Kiểu dữ liệu Dashboard — mirror DTO backend (`/api/dashboard/*`). */

/** Một điểm chuỗi thời gian (biểu đồ area/line/cột). */
export interface TimeSeriesPoint {
    period: string; // "2026-07"
    value: number;
}

/** Một phần biểu đồ tròn / thanh xếp chồng kèm số & %. */
export interface DonutSegment {
    label: string;
    count: number;
    pct: number;
}

/** KPI kèm so sánh kỳ trước. */
export interface KpiMetric {
    current: number;
    previous: number;
    growthPct: number | null;
}

/** Một hàng thanh ngang xếp chồng theo nhóm. */
export interface GroupedStatusRow {
    group: string;
    segments: DonutSegment[];
}

/** Một bậc phễu chuyển đổi. */
export interface FunnelStage {
    label: string;
    count: number;
    pctOfFirst: number;
}

/** Một mục danh sách xếp hạng theo giá trị. */
export interface RankedItem {
    refId: number | null;
    label: string;
    value: number;
}

/** Một mục việc gấp. */
export interface UrgentItem {
    type: 'ticket' | 'quotation' | 'invoice';
    refId: number | null;
    code: string;
    label: string;
    dueInfo: string;
}

/** Dữ liệu dashboard ADMIN. */
export interface AdminDashboard {
    userTotal: KpiMetric;
    usersByStatus: DonutSegment[];
    newUsersThisMonth: KpiMetric;
    usersByMonth: TimeSeriesPoint[];
    roleCount: number;
    permissionCount: number;
    usersByRole: DonutSegment[];
    recordTotals: DonutSegment[];
}

/**
 * Dữ liệu dashboard kinh doanh (manager toàn đội / sale cá nhân) — mirror
 * SalesDashboardResult.java bản đã trim. 9 field dưới đây bị BE cắt (comment) vì không FE nào đọc
 * tới — mở lại đồng thời với BE (SalesDashboardResult.java + DashboardRepositoryImpl.getSales()).
 */
export interface SalesDashboard {
    // totalRevenue: KpiMetric;
    // totalCost: KpiMetric;
    // totalProfit: KpiMetric;
    // revenueByMonth: TimeSeriesPoint[];
    // costByMonth: TimeSeriesPoint[];
    // profitByMonth: TimeSeriesPoint[];
    oppTotal: KpiMetric;
    oppOpen: KpiMetric;
    oppWon: KpiMetric;
    oppLost: KpiMetric;
    winRate: KpiMetric;
    conversionFunnel: FunnelStage[];
    topOpportunities: RankedItem[];
    opportunitiesByStatus: DonutSegment[];
    ordersByStatus: DonutSegment[];
    invoicesByStatus: DonutSegment[];
    ticketsByStatus: DonutSegment[];
    // urgentItems: UrgentItem[];
    // teamByOwner: GroupedStatusRow[] | null;
    // revenueByOwner: RankedItem[] | null;
}

/** Mã kỳ thống kê. */
export type DashboardPeriod = 'month' | 'quarter' | 'year';

/** CAC (chi phí/lead, /cơ hội, /đơn hàng) của một chiến dịch trong kỳ. */
export interface CampaignCacRow {
    campaignId: number;
    name: string;
    actualCost: number;
    leadCount: number;
    opportunityCount: number;
    orderCount: number;
    costPerLead: number | null;
    costPerOpportunity: number | null;
    costPerOrder: number | null;
}

/** Danh sách xếp hạng top-N kèm tổng số đầy đủ. */
export interface CountedRankedList {
    total: number;
    items: RankedItem[];
}

/** Một hàng bảng xếp hạng nhân viên theo hiệu suất chăm sóc cơ hội. */
export interface EmployeeWinRateRow {
    userId: number;
    fullName: string;
    revenue: number;
    wonCount: number;
    lostCount: number;
    winRatePct: number;
}

/** So sánh doanh thu kỳ này/kỳ trước của một chiến dịch đang chạy. */
export interface CampaignRevenueComparisonRow {
    campaignId: number;
    name: string;
    revenue: KpiMetric;
}
