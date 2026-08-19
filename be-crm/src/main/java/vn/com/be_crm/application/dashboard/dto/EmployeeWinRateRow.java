package vn.com.be_crm.application.dashboard.dto;

import java.math.BigDecimal;

/**
 * Một hàng trong bảng xếp hạng nhân viên theo hiệu suất chăm sóc cơ hội trong kỳ.
 * Gộp cả doanh thu để thay thế danh sách "doanh thu theo nhân viên" cũ.
 *
 * @param userId     ID nhân viên
 * @param fullName   tên nhân viên
 * @param revenue    tổng doanh thu hóa đơn của nhân viên trong kỳ
 * @param wonCount   số cơ hội thắng trong kỳ
 * @param lostCount  số cơ hội thua trong kỳ
 * @param winRatePct tỉ lệ thắng % = wonCount / (wonCount + lostCount) * 100
 */
public record EmployeeWinRateRow(
        Long userId,
        String fullName,
        BigDecimal revenue,
        long wonCount,
        long lostCount,
        BigDecimal winRatePct
) {
}
