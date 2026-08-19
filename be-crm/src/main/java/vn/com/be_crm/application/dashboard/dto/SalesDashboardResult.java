package vn.com.be_crm.application.dashboard.dto;

import java.util.List;

/**
 * Dữ liệu Dashboard kinh doanh — hiện chỉ phục vụ SALES_STAFF (cá nhân, endpoint {@code /sale}).
 * Bản đầy đủ (toàn đội cho SALES_MANAGER, {@code totalRevenue/totalCost/totalProfit/*ByMonth/
 * urgentItems/teamByOwner/revenueByOwner}) đã cắt — xem comment trên khai báo record bên dưới.
 *
 * @param oppTotal              tổng số cơ hội (kèm % tăng)
 * @param oppOpen               số cơ hội đang thực hiện
 * @param oppWon                số cơ hội thắng
 * @param oppLost               số cơ hội thua
 * @param winRate               tỉ lệ thắng % (kèm % tăng)
 * @param conversionFunnel      phễu chuyển đổi theo giai đoạn
 * @param topOpportunities      cơ hội giá trị lớn
 * @param opportunitiesByStatus cơ hội theo trạng thái
 * @param ordersByStatus        đơn hàng theo trạng thái
 * @param invoicesByStatus      hóa đơn theo trạng thái
 * @param ticketsByStatus       phiếu chăm sóc theo trạng thái
 */
// 9 field dưới đây (totalRevenue...profitByMonth, urgentItems, teamByOwner, revenueByOwner) đã bị
// cắt khỏi record vì không FE nào đọc tới (ManagerDashboardView redesign bỏ hẳn endpoint /manager,
// StaffDashboardView cố ý không lấy các field này). Mở lại: bỏ comment các dòng field bên dưới +
// khối tính tương ứng trong DashboardRepositoryImpl.getSales() + 4 hàm helper đã comment
// (cogsSql/urgentItems/teamByOwner/revenueByOwner) + DashboardController.manager() (BE) + phần FE
// tương ứng (dashboardTypes.ts, RevenueCostProfitChart.tsx, UrgentList.tsx, useManagerDashboard.ts).
public record SalesDashboardResult(
        // KpiMetric totalRevenue,
        // KpiMetric totalCost,
        // KpiMetric totalProfit,
        // List<TimeSeriesPoint> revenueByMonth,
        // List<TimeSeriesPoint> costByMonth,
        // List<TimeSeriesPoint> profitByMonth,
        KpiMetric oppTotal,
        KpiMetric oppOpen,
        KpiMetric oppWon,
        KpiMetric oppLost,
        KpiMetric winRate,
        List<FunnelStage> conversionFunnel,
        List<RankedItem> topOpportunities,
        List<DonutSegment> opportunitiesByStatus,
        List<DonutSegment> ordersByStatus,
        List<DonutSegment> invoicesByStatus,
        List<DonutSegment> ticketsByStatus
        // List<UrgentItem> urgentItems,
        // List<GroupedStatusRow> teamByOwner,
        // List<RankedItem> revenueByOwner
) {
}
