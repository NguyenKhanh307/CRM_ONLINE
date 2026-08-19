package vn.com.be_crm.application.copilot.dto;

import java.math.BigDecimal;

/**
 * Một lát của biểu đồ tròn so sánh ở trang /phan-tich.
 *
 * @param label nhãn hiển thị (tên nhân viên/chiến dịch/khách hàng/sản phẩm, hoặc "Kỳ này"/"Kỳ trước")
 * @param value giá trị (doanh thu)
 */
public record CopilotChartSegment(String label, BigDecimal value) {
}
