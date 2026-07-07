package vn.com.be_crm.application.dashboard.dto;

import java.util.List;

/**
 * Dữ liệu Dashboard cho vai trò ADMIN — sức khỏe hệ thống: tài khoản, vai trò, quyền, cơ cấu tổ chức.
 *
 * @param userTotal          tổng số tài khoản (kèm % tăng so kỳ trước)
 * @param usersByStatus      phân bổ tài khoản theo trạng thái (active/inactive/locked)
 * @param newUsersThisMonth  số tài khoản mới trong kỳ (kèm % tăng)
 * @param usersByMonth       số tài khoản tạo theo tháng (12 tháng gần nhất)
 * @param roleCount          tổng số vai trò
 * @param permissionCount    tổng số quyền
 * @param usersByRole        phân bổ tài khoản theo vai trò
 * @param usersByUnit        phân bổ tài khoản theo đơn vị
 * @param recordTotals       tổng quan số bản ghi các phân hệ nghiệp vụ toàn hệ thống
 */
public record AdminDashboardResult(
        KpiMetric userTotal,
        List<DonutSegment> usersByStatus,
        KpiMetric newUsersThisMonth,
        List<TimeSeriesPoint> usersByMonth,
        long roleCount,
        long permissionCount,
        List<DonutSegment> usersByRole,
        List<DonutSegment> usersByUnit,
        List<DonutSegment> recordTotals
) {
}
