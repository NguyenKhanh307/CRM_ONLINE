package vn.com.be_crm.application.copilot.dto;

import java.util.List;

/**
 * Dữ liệu biểu đồ tròn đính kèm nút "Xem biểu đồ so sánh" — FE dựng đúng 1 biểu đồ tại /phan-tich,
 * không cần gọi thêm API nào.
 *
 * @param title    tiêu đề biểu đồ (vd "Doanh thu theo chiến dịch")
 * @param segments các lát biểu đồ
 */
public record CopilotChartData(String title, List<CopilotChartSegment> segments) {
}
