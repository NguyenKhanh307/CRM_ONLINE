package vn.com.be_crm.application.dashboard.dto;

import java.math.BigDecimal;

/**
 * Một điểm dữ liệu chuỗi thời gian dùng cho biểu đồ area/line/cột.
 *
 * @param period nhãn kỳ, vd "2026-07" (theo tháng)
 * @param value  giá trị tại kỳ đó
 */
public record TimeSeriesPoint(String period, BigDecimal value) {
}
