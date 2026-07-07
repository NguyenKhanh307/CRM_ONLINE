package vn.com.be_crm.domain.dashboard.model;

import java.time.LocalDate;

/**
 * Khoảng thời gian nửa mở [from, toExclusive) dùng cho truy vấn thống kê.
 * Dùng cận trên mở để so khớp đồng nhất cho cả cột DATE và DATETIME
 * (điều kiện {@code >= :from AND < :toExclusive}).
 *
 * @param from        mốc bắt đầu (bao gồm)
 * @param toExclusive mốc kết thúc (không bao gồm)
 */
public record DateRange(LocalDate from, LocalDate toExclusive) {
}
