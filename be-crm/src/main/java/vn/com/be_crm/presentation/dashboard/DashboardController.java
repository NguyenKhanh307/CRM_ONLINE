package vn.com.be_crm.presentation.dashboard;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
import vn.com.be_crm.application.dashboard.query.GetAdminDashboardUseCase;
import vn.com.be_crm.application.dashboard.query.GetCampaignAttributedRevenueUseCase;
import vn.com.be_crm.application.dashboard.query.GetCampaignCacUseCase;
import vn.com.be_crm.application.dashboard.query.GetCampaignPeriodComparisonUseCase;
import vn.com.be_crm.application.dashboard.query.GetCampaignRoiUseCase;
import vn.com.be_crm.application.dashboard.query.GetLeadPoolUseCase;
import vn.com.be_crm.application.dashboard.query.GetLossReasonsByOwnerUseCase;
import vn.com.be_crm.application.dashboard.query.GetOpportunitySourceUseCase;
import vn.com.be_crm.application.dashboard.query.GetRevenueByCampaignUseCase;
import vn.com.be_crm.application.dashboard.query.GetSalesDashboardUseCase;
import vn.com.be_crm.application.dashboard.query.GetStalledByOwnerUseCase;
import vn.com.be_crm.application.dashboard.query.GetStalledOpportunitiesUseCase;
import vn.com.be_crm.application.dashboard.query.GetWinRateLeaderboardUseCase;
import vn.com.be_crm.application.dashboard.query.GetWinRateTrendUseCase;
import vn.com.be_crm.application.dashboard.query.SalesDashboardQuery;
import vn.com.be_crm.core.response.ApiResponse;
import vn.com.be_crm.core.util.SecurityUtils;

import java.util.List;

/**
 * REST controller cho Dashboard "Bàn làm việc" + trang phân tích "/phan-tich" — dữ liệu thống kê
 * phân theo vai trò. Endpoint toàn-công-ty (campaign ROI/CAC, lead pool, quy kết doanh thu, so sánh
 * kỳ theo chiến dịch, bảng xếp hạng nhân viên, số cơ hội treo theo nhân viên) chỉ ADMIN/SALES_MANAGER
 * truy cập; endpoint có bản cá nhân (tỉ lệ thắng theo thời gian, cơ hội treo, lý do thua) mọi user đã
 * đăng nhập truy cập được — tự lọc theo owner nếu không phải ADMIN/SALES_MANAGER.
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final GetAdminDashboardUseCase adminUC;
    private final GetSalesDashboardUseCase salesUC;
    private final GetRevenueByCampaignUseCase revenueByCampaignUC;
    private final GetCampaignRoiUseCase campaignRoiUC;
    private final GetCampaignCacUseCase campaignCacUC;
    private final GetWinRateTrendUseCase winRateTrendUC;
    private final GetStalledOpportunitiesUseCase stalledOpportunitiesUC;
    private final GetOpportunitySourceUseCase opportunitySourceUC;
    private final GetLeadPoolUseCase leadPoolUC;
    private final GetCampaignAttributedRevenueUseCase campaignAttributedRevenueUC;
    private final GetCampaignPeriodComparisonUseCase campaignPeriodComparisonUC;
    private final GetWinRateLeaderboardUseCase winRateLeaderboardUC;
    private final GetLossReasonsByOwnerUseCase lossReasonsByOwnerUC;
    private final GetStalledByOwnerUseCase stalledByOwnerUC;

    public DashboardController(GetAdminDashboardUseCase adminUC, GetSalesDashboardUseCase salesUC,
                               GetRevenueByCampaignUseCase revenueByCampaignUC, GetCampaignRoiUseCase campaignRoiUC,
                               GetCampaignCacUseCase campaignCacUC, GetWinRateTrendUseCase winRateTrendUC,
                               GetStalledOpportunitiesUseCase stalledOpportunitiesUC, GetOpportunitySourceUseCase opportunitySourceUC,
                               GetLeadPoolUseCase leadPoolUC, GetCampaignAttributedRevenueUseCase campaignAttributedRevenueUC,
                               GetCampaignPeriodComparisonUseCase campaignPeriodComparisonUC, GetWinRateLeaderboardUseCase winRateLeaderboardUC,
                               GetLossReasonsByOwnerUseCase lossReasonsByOwnerUC, GetStalledByOwnerUseCase stalledByOwnerUC) {
        this.adminUC = adminUC;
        this.salesUC = salesUC;
        this.revenueByCampaignUC = revenueByCampaignUC;
        this.campaignRoiUC = campaignRoiUC;
        this.campaignCacUC = campaignCacUC;
        this.winRateTrendUC = winRateTrendUC;
        this.stalledOpportunitiesUC = stalledOpportunitiesUC;
        this.opportunitySourceUC = opportunitySourceUC;
        this.leadPoolUC = leadPoolUC;
        this.campaignAttributedRevenueUC = campaignAttributedRevenueUC;
        this.campaignPeriodComparisonUC = campaignPeriodComparisonUC;
        this.winRateLeaderboardUC = winRateLeaderboardUC;
        this.lossReasonsByOwnerUC = lossReasonsByOwnerUC;
        this.stalledByOwnerUC = stalledByOwnerUC;
    }

    /** Dashboard cho ADMIN — sức khỏe hệ thống. Chỉ ADMIN mới truy cập. */
    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<AdminDashboardResult>> admin(@RequestParam(defaultValue = "year") String period) {
        if (!SecurityUtils.isAdmin(auth())) return forbidden();
        return ResponseEntity.ok(ApiResponse.ok(adminUC.execute(period)));
    }

    // Tắt — ManagerDashboardView.tsx (FE) đã redesign, chuyển sang 11 hook riêng (campaign
    // ROI/CAC, win-rate leaderboard...), không còn gọi /api/dashboard/manager nữa. Mở lại: bỏ
    // comment method này + useManagerDashboard.ts/dashboardService.getManager() (FE) + 3 field
    // teamByOwner/revenueByOwner/totalRevenue.../ đã trim khỏi SalesDashboardResult (BE).
    // /** Dashboard cho SALES_MANAGER — hiệu quả kinh doanh toàn đội. ADMIN/SALES_MANAGER truy cập. */
    // @GetMapping("/manager")
    // public ResponseEntity<ApiResponse<SalesDashboardResult>> manager(@RequestParam(defaultValue = "year") String period) {
    //     if (!SecurityUtils.isAdminOrManager(auth())) return forbidden();
    //     return ResponseEntity.ok(ApiResponse.ok(salesUC.execute(new SalesDashboardQuery(null, true, period))));
    // }

    /** Dashboard cho SALES_STAFF — dữ liệu cá nhân (owner = người đăng nhập). */
    @GetMapping("/sale")
    public ResponseEntity<ApiResponse<SalesDashboardResult>> sale(@RequestParam(defaultValue = "year") String period,
                                                                  HttpServletRequest req) {
        Long userId = (Long) req.getAttribute("userId");
        return ResponseEntity.ok(ApiResponse.ok(salesUC.execute(new SalesDashboardQuery(userId, false, period))));
    }

    /**
     * Doanh thu theo chiến dịch trong kỳ — trang phân tích so sánh. ADMIN/SALES_MANAGER xem toàn bộ,
     * nhân viên chỉ xem hóa đơn mình phụ trách.
     */
    @GetMapping("/revenue-by-campaign")
    public ResponseEntity<ApiResponse<List<RankedItem>>> revenueByCampaign(
            @RequestParam(defaultValue = "quarter") String period, HttpServletRequest req) {
        Long ownerId = privileged() ? null : userId(req);
        return ResponseEntity.ok(ApiResponse.ok(
                revenueByCampaignUC.execute(new GetRevenueByCampaignUseCase.Query(ownerId, period))));
    }

    /** Top chiến dịch theo ROI trong kỳ. Chỉ ADMIN/SALES_MANAGER. */
    @GetMapping("/campaign-roi")
    public ResponseEntity<ApiResponse<List<RankedItem>>> campaignRoi(@RequestParam(defaultValue = "quarter") String period) {
        if (!privileged()) return forbidden();
        return ResponseEntity.ok(ApiResponse.ok(campaignRoiUC.execute(period)));
    }

    /** Chi phí trên mỗi lead/cơ hội/đơn hàng (CAC) theo từng chiến dịch trong kỳ. Chỉ ADMIN/SALES_MANAGER. */
    @GetMapping("/campaign-cac")
    public ResponseEntity<ApiResponse<List<CampaignCacRow>>> campaignCac(@RequestParam(defaultValue = "quarter") String period) {
        if (!privileged()) return forbidden();
        return ResponseEntity.ok(ApiResponse.ok(campaignCacUC.execute(period)));
    }

    /** Tỉ lệ thắng-thua theo thời gian (12 tháng gần nhất). Toàn đội (privileged) hoặc cá nhân (sale). */
    @GetMapping("/win-rate-trend")
    public ResponseEntity<ApiResponse<List<TimeSeriesPoint>>> winRateTrend(@RequestParam(defaultValue = "quarter") String period,
                                                             HttpServletRequest req) {
        Long ownerId = privileged() ? null : userId(req);
        return ResponseEntity.ok(ApiResponse.ok(
                winRateTrendUC.execute(new GetWinRateTrendUseCase.Query(ownerId, period))));
    }

    /** Cơ hội "treo" — đang mở, không có hoạt động chăm sóc trong N ngày. Toàn đội hoặc cá nhân. */
    @GetMapping("/stalled-opportunities")
    public ResponseEntity<ApiResponse<CountedRankedList>> stalledOpportunities(
            @RequestParam(required = false) Integer days, HttpServletRequest req) {
        Long ownerId = privileged() ? null : userId(req);
        return ResponseEntity.ok(ApiResponse.ok(
                stalledOpportunitiesUC.execute(new GetStalledOpportunitiesUseCase.Query(ownerId, days))));
    }

    /** Tỉ lệ cơ hội có nguồn từ chiến dịch vs tự phát sinh trong kỳ. Chỉ ADMIN/SALES_MANAGER. */
    @GetMapping("/opportunity-source")
    public ResponseEntity<ApiResponse<List<DonutSegment>>> opportunitySource(@RequestParam(defaultValue = "quarter") String period) {
        if (!privileged()) return forbidden();
        return ResponseEntity.ok(ApiResponse.ok(opportunitySourceUC.execute(period)));
    }

    /** Lead đang ở pool chung, chưa ai nhận. Chỉ ADMIN/SALES_MANAGER. */
    @GetMapping("/lead-pool")
    public ResponseEntity<ApiResponse<CountedRankedList>> leadPool() {
        if (!privileged()) return forbidden();
        return ResponseEntity.ok(ApiResponse.ok(leadPoolUC.execute(null)));
    }

    /** % doanh thu quy kết được về marketing trên tổng doanh thu, kỳ này vs kỳ trước. Chỉ ADMIN/SALES_MANAGER. */
    @GetMapping("/campaign-attributed-revenue")
    public ResponseEntity<ApiResponse<KpiMetric>> campaignAttributedRevenue(@RequestParam(defaultValue = "quarter") String period) {
        if (!privileged()) return forbidden();
        return ResponseEntity.ok(ApiResponse.ok(campaignAttributedRevenueUC.execute(period)));
    }

    /** So sánh doanh thu kỳ này/kỳ trước cho các chiến dịch đang chạy song song. Chỉ ADMIN/SALES_MANAGER. */
    @GetMapping("/campaign-period-comparison")
    public ResponseEntity<ApiResponse<List<CampaignRevenueComparisonRow>>> campaignPeriodComparison(
            @RequestParam(defaultValue = "quarter") String period) {
        if (!privileged()) return forbidden();
        return ResponseEntity.ok(ApiResponse.ok(campaignPeriodComparisonUC.execute(period)));
    }

    /** Bảng xếp hạng nhân viên theo tỉ lệ thắng (kèm doanh thu) trong kỳ. Chỉ ADMIN/SALES_MANAGER. */
    @GetMapping("/win-rate-leaderboard")
    public ResponseEntity<ApiResponse<List<EmployeeWinRateRow>>> winRateLeaderboard(@RequestParam(defaultValue = "quarter") String period) {
        if (!privileged()) return forbidden();
        return ResponseEntity.ok(ApiResponse.ok(winRateLeaderboardUC.execute(period)));
    }

    /** Lý do thua theo từng nhân viên trong kỳ. Toàn đội (nhiều nhóm) hoặc cá nhân (1 nhóm). */
    @GetMapping("/loss-reasons-by-owner")
    public ResponseEntity<ApiResponse<List<GroupedStatusRow>>> lossReasonsByOwner(
            @RequestParam(defaultValue = "quarter") String period, HttpServletRequest req) {
        Long ownerId = privileged() ? null : userId(req);
        return ResponseEntity.ok(ApiResponse.ok(
                lossReasonsByOwnerUC.execute(new GetLossReasonsByOwnerUseCase.Query(ownerId, period))));
    }

    /** Số cơ hội "treo" theo từng nhân viên (top 8). Chỉ ADMIN/SALES_MANAGER (sale dùng lại stalled-opportunities). */
    @GetMapping("/stalled-by-owner")
    public ResponseEntity<ApiResponse<List<RankedItem>>> stalledByOwner(@RequestParam(required = false) Integer days) {
        if (!privileged()) return forbidden();
        return ResponseEntity.ok(ApiResponse.ok(stalledByOwnerUC.execute(days)));
    }

    /** true nếu người dùng hiện tại là ADMIN hoặc SALES_MANAGER. */
    private boolean privileged() {
        return SecurityUtils.isAdminOrManager(auth());
    }

    /** Authentication hiện tại từ SecurityContext. */
    private Authentication auth() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /** userId hiện tại từ JWT (gán bởi JwtAuthFilter vào request attribute). */
    private Long userId(HttpServletRequest req) {
        return (Long) req.getAttribute("userId");
    }

    /** Trả 403 Forbidden với ApiResponse chuẩn. */
    private <T> ResponseEntity<ApiResponse<T>> forbidden() {
        return ResponseEntity.status(403).body(ApiResponse.error("Không có quyền truy cập bảng điều khiển này", 403));
    }
}
