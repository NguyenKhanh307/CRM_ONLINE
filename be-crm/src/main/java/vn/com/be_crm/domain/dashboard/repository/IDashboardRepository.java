package vn.com.be_crm.domain.dashboard.repository;

import vn.com.be_crm.application.dashboard.dto.AdminDashboardResult;
import vn.com.be_crm.application.dashboard.dto.CampaignCacRow;
import vn.com.be_crm.application.dashboard.dto.CampaignRevenueComparisonRow;
import vn.com.be_crm.application.dashboard.dto.CountedRankedList;
import vn.com.be_crm.application.dashboard.dto.DonutSegment;
import vn.com.be_crm.application.dashboard.dto.EmployeeWinRateRow;
import vn.com.be_crm.application.dashboard.dto.GroupedStatusRow;
import vn.com.be_crm.application.dashboard.dto.KpiMetric;
import vn.com.be_crm.application.dashboard.dto.RankedItem;
import vn.com.be_crm.application.dashboard.dto.SalesDashboardResult;
import vn.com.be_crm.application.dashboard.dto.TimeSeriesPoint;
import vn.com.be_crm.domain.dashboard.model.DateRange;

import java.time.LocalDate;
import java.util.List;

/**
 * Port truy vấn thống kê tổng hợp cho Dashboard (chỉ đọc, native COUNT/SUM).
 */
public interface IDashboardRepository {

    /**
     * Thống kê hệ thống cho ADMIN.
     *
     * @param cur        kỳ hiện tại (cho KPI + phân bổ)
     * @param prev       kỳ liền trước (để tính % tăng trưởng)
     * @param seriesFrom mốc bắt đầu chuỗi 12 tháng cho biểu đồ cột theo tháng
     * @return dữ liệu dashboard admin
     */
    AdminDashboardResult getAdmin(DateRange cur, DateRange prev, LocalDate seriesFrom);

    /**
     * Thống kê kinh doanh cho SALES_MANAGER (toàn đội) hoặc SALES_STAFF (cá nhân).
     *
     * @param ownerId     null = toàn đội (manager); khác null = lọc theo owner (sale)
     * @param includeTeam true = tính thêm teamByOwner/revenueByOwner (manager)
     * @param cur         kỳ hiện tại
     * @param prev        kỳ liền trước
     * @param seriesFrom  mốc bắt đầu chuỗi 12 tháng cho biểu đồ theo tháng
     * @return dữ liệu dashboard kinh doanh
     */
    SalesDashboardResult getSales(Long ownerId, boolean includeTeam, DateRange cur, DateRange prev, LocalDate seriesFrom);

    /**
     * Doanh thu theo chiến dịch trong kỳ (top 8) — phục vụ trang phân tích so sánh.
     *
     * @param ownerId null = toàn bộ; khác null = lọc hóa đơn theo owner
     * @param cur     kỳ thống kê
     * @return danh sách xếp hạng {campaignId, tên chiến dịch, tổng doanh thu}
     */
    List<RankedItem> revenueByCampaign(Long ownerId, DateRange cur);

    /**
     * Top chiến dịch theo ROI = (doanh thu − chi phí thực tế) / chi phí thực tế trong kỳ.
     *
     * @param cur kỳ thống kê
     * @return danh sách xếp hạng {campaignId, tên, roi%}
     */
    List<RankedItem> campaignRoi(DateRange cur);

    /**
     * Chi phí trên mỗi lead/cơ hội/đơn hàng (CAC) theo từng chiến dịch trong kỳ.
     *
     * @param cur kỳ thống kê
     * @return danh sách CAC theo chiến dịch
     */
    List<CampaignCacRow> campaignCac(DateRange cur);

    /**
     * Tỉ lệ thắng-thua theo tháng trong 12 tháng gần nhất.
     *
     * @param ownerId    null = toàn đội; khác null = lọc theo owner
     * @param seriesFrom mốc bắt đầu chuỗi 12 tháng
     * @return chuỗi tỉ lệ thắng % theo tháng
     */
    List<TimeSeriesPoint> winRateTrend(Long ownerId, LocalDate seriesFrom);

    /**
     * Cơ hội "treo" — đang mở, không có hoạt động chăm sóc trong N ngày.
     *
     * @param ownerId null = toàn đội; khác null = lọc theo owner
     * @param days    ngưỡng số ngày không hoạt động
     * @return tổng số + top 10 cơ hội treo lâu nhất
     */
    CountedRankedList stalledOpportunities(Long ownerId, int days);

    /**
     * Tỉ lệ cơ hội có nguồn từ chiến dịch vs tự phát sinh trong kỳ.
     *
     * @param cur kỳ thống kê
     * @return donut 2 phần tử (Từ chiến dịch / Tự phát sinh)
     */
    List<DonutSegment> opportunitySource(DateRange cur);

    /**
     * Lead đang ở pool chung (chưa ai nhận).
     *
     * @return tổng số + top 10 lead chờ lâu nhất
     */
    CountedRankedList leadPool();

    /**
     * % doanh thu có gắn campaign_id (quy kết được về marketing) trên tổng doanh thu, kỳ này vs kỳ trước.
     *
     * @param cur  kỳ hiện tại
     * @param prev kỳ liền trước
     * @return KpiMetric phần trăm quy kết
     */
    KpiMetric campaignAttributedRevenuePct(DateRange cur, DateRange prev);

    /**
     * So sánh doanh thu kỳ này vs kỳ trước cho các chiến dịch đang chạy (status='running').
     *
     * @param cur  kỳ hiện tại
     * @param prev kỳ liền trước
     * @return danh sách so sánh theo chiến dịch
     */
    List<CampaignRevenueComparisonRow> campaignPeriodComparison(DateRange cur, DateRange prev);

    /**
     * Bảng xếp hạng nhân viên theo tỉ lệ thắng (kèm doanh thu) trong kỳ.
     *
     * @param cur kỳ thống kê
     * @return danh sách xếp hạng theo nhân viên
     */
    List<EmployeeWinRateRow> winRateLeaderboard(DateRange cur);

    /**
     * Lý do thua theo từng nhân viên trong kỳ.
     *
     * @param ownerId null = toàn đội (nhiều group); khác null = chỉ 1 group (chính họ)
     * @param cur     kỳ thống kê
     * @return danh sách nhóm theo nhân viên, mỗi nhóm là donut lý do thua
     */
    List<GroupedStatusRow> lossReasonsByOwner(Long ownerId, DateRange cur);

    /**
     * Số cơ hội "treo" theo từng nhân viên (toàn đội, top 8).
     *
     * @param days ngưỡng số ngày không hoạt động
     * @return danh sách xếp hạng theo nhân viên
     */
    List<RankedItem> stalledByOwner(int days);
}
