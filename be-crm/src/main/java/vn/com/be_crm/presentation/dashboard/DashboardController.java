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
import vn.com.be_crm.application.dashboard.dto.SalesDashboardResult;
import vn.com.be_crm.application.dashboard.query.GetAdminDashboardUseCase;
import vn.com.be_crm.application.dashboard.query.GetSalesDashboardUseCase;
import vn.com.be_crm.application.dashboard.query.SalesDashboardQuery;
import vn.com.be_crm.presentation.shared.ApiResponse;

/**
 * REST controller cho Dashboard "Bàn làm việc" — dữ liệu thống kê phân theo vai trò.
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final GetAdminDashboardUseCase adminUC;
    private final GetSalesDashboardUseCase salesUC;

    /** @param adminUC use case dashboard admin @param salesUC use case dashboard kinh doanh */
    public DashboardController(GetAdminDashboardUseCase adminUC, GetSalesDashboardUseCase salesUC) {
        this.adminUC = adminUC;
        this.salesUC = salesUC;
    }

    /**
     * Dashboard cho ADMIN — sức khỏe hệ thống. Chỉ ADMIN mới truy cập.
     *
     * @param period mã kỳ (month|quarter|year)
     * @return 200 kèm dữ liệu, hoặc 403 nếu không phải ADMIN
     */
    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<AdminDashboardResult>> admin(@RequestParam(defaultValue = "year") String period) {
        if (!hasRole("ADMIN")) return forbidden();
        return ResponseEntity.ok(ApiResponse.ok(adminUC.execute(period)));
    }

    /**
     * Dashboard cho SALES_MANAGER — hiệu quả kinh doanh toàn đội. ADMIN/SALES_MANAGER truy cập.
     *
     * @param period mã kỳ (month|quarter|year)
     * @return 200 kèm dữ liệu, hoặc 403 nếu không đủ quyền
     */
    @GetMapping("/manager")
    public ResponseEntity<ApiResponse<SalesDashboardResult>> manager(@RequestParam(defaultValue = "year") String period) {
        if (!hasRole("ADMIN") && !hasRole("SALES_MANAGER")) return forbidden();
        return ResponseEntity.ok(ApiResponse.ok(salesUC.execute(new SalesDashboardQuery(null, true, period))));
    }

    /**
     * Dashboard cho SALES_STAFF — dữ liệu cá nhân (owner = người đăng nhập).
     *
     * @param period mã kỳ (month|quarter|year)
     * @param req    HTTP request (lấy userId từ JWT)
     * @return 200 kèm dữ liệu cá nhân
     */
    @GetMapping("/sale")
    public ResponseEntity<ApiResponse<SalesDashboardResult>> sale(@RequestParam(defaultValue = "year") String period,
                                                                  HttpServletRequest req) {
        Long userId = (Long) req.getAttribute("userId");
        return ResponseEntity.ok(ApiResponse.ok(salesUC.execute(new SalesDashboardQuery(userId, false, period))));
    }

    /** Kiểm tra người dùng hiện tại có authority (role name) chỉ định không. */
    private boolean hasRole(String role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(role));
    }

    /** Trả 403 Forbidden với ApiResponse chuẩn. */
    private <T> ResponseEntity<ApiResponse<T>> forbidden() {
        return ResponseEntity.status(403).body(ApiResponse.error("Không có quyền truy cập bảng điều khiển này", 403));
    }
}
