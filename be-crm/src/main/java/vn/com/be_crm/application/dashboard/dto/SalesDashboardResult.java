package vn.com.be_crm.application.dashboard.dto;

import java.util.List;

/**
 * Dữ liệu Dashboard cho vai trò SALES_MANAGER (toàn đội) hoặc SALES_STAFF (cá nhân).
 * Manager có thêm {@code teamByOwner}/{@code revenueByOwner}; với Sale các trường này = null.
 *
 * @param totalRevenue          tổng doanh thu (kèm % tăng)
 * @param totalCost             tổng chi phí ≈ giá vốn (COGS) (kèm % tăng)
 * @param totalProfit           tổng lợi nhuận = doanh thu − chi phí (kèm % tăng)
 * @param revenueByMonth        doanh thu theo tháng
 * @param costByMonth           chi phí theo tháng
 * @param profitByMonth         lợi nhuận theo tháng
 * @param oppTotal              tổng số cơ hội (kèm % tăng)
 * @param oppOpen               số cơ hội đang thực hiện
 * @param oppWon                số cơ hội thắng
 * @param oppLost               số cơ hội thua
 * @param winRate               tỉ lệ thắng % (kèm % tăng)
 * @param expectedByMonth       doanh số kỳ vọng theo tháng (pipeline)
 * @param conversionFunnel      phễu chuyển đổi theo giai đoạn
 * @param topOpportunities      cơ hội giá trị lớn
 * @param leadsByStatus         tiềm năng theo trạng thái
 * @param opportunitiesByStatus cơ hội theo trạng thái
 * @param ordersByStatus        đơn hàng theo trạng thái
 * @param invoicesByStatus      hóa đơn theo trạng thái
 * @param ticketsByStatus       phiếu chăm sóc theo trạng thái
 * @param urgentItems           danh sách việc gấp
 * @param teamByOwner           thống kê theo nhân viên (chỉ manager, null với sale)
 * @param revenueByOwner        doanh thu theo nhân viên (chỉ manager, null với sale)
 */
public record SalesDashboardResult(
        KpiMetric totalRevenue,
        KpiMetric totalCost,
        KpiMetric totalProfit,
        List<TimeSeriesPoint> revenueByMonth,
        List<TimeSeriesPoint> costByMonth,
        List<TimeSeriesPoint> profitByMonth,
        KpiMetric oppTotal,
        KpiMetric oppOpen,
        KpiMetric oppWon,
        KpiMetric oppLost,
        KpiMetric winRate,
        List<TimeSeriesPoint> expectedByMonth,
        List<FunnelStage> conversionFunnel,
        List<RankedItem> topOpportunities,
        List<DonutSegment> leadsByStatus,
        List<DonutSegment> opportunitiesByStatus,
        List<DonutSegment> ordersByStatus,
        List<DonutSegment> invoicesByStatus,
        List<DonutSegment> ticketsByStatus,
        List<UrgentItem> urgentItems,
        List<GroupedStatusRow> teamByOwner,
        List<RankedItem> revenueByOwner
) {
}
