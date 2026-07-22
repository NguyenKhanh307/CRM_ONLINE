package vn.com.be_crm.domain.dashboard.repository;

import vn.com.be_crm.application.dashboard.dto.AdminDashboardResult;
import vn.com.be_crm.application.dashboard.dto.SalesDashboardResult;
import vn.com.be_crm.domain.dashboard.model.DateRange;

import java.time.LocalDate;

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
    java.util.List<vn.com.be_crm.application.dashboard.dto.RankedItem> revenueByCampaign(Long ownerId, DateRange cur);
}
