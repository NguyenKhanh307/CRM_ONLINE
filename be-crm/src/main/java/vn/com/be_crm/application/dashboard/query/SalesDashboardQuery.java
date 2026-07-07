package vn.com.be_crm.application.dashboard.query;

/**
 * Tham số truy vấn Dashboard kinh doanh.
 *
 * @param ownerId     null = toàn đội (manager); khác null = lọc theo owner (sale)
 * @param includeTeam true = tính thêm thống kê theo nhân viên (manager)
 * @param period      mã kỳ ("month" | "quarter" | "year")
 */
public record SalesDashboardQuery(Long ownerId, boolean includeTeam, String period) {
}
