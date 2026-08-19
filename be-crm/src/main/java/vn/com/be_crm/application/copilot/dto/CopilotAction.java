package vn.com.be_crm.application.copilot.dto;

/**
 * Hành động đính kèm câu trả lời của trợ lý AI để frontend thực thi.
 *
 * @param type  "navigate" = FE tự điều hướng ngay; "link" = FE hiện nút, bấm mới điều hướng
 * @param route đường dẫn nội bộ FE (vd "/khach-hang", "/phan-tich")
 * @param label nhãn hiển thị cho nút (dùng khi type = "link")
 * @param chart dữ liệu biểu đồ tròn đính kèm (chỉ có ở action link "Xem biểu đồ so sánh" khi dò được
 *              dữ liệu phù hợp câu hỏi) — null nếu không áp dụng
 */
public record CopilotAction(String type, String route, String label, CopilotChartData chart) {

    /** Tạo action điều hướng ngay (FE tự nhảy trang). @param route đường dẫn @return action navigate */
    public static CopilotAction navigate(String route) {
        return new CopilotAction("navigate", route, null, null);
    }

    /** Tạo action dạng nút link (bấm mới mở). @param route đường dẫn @param label nhãn nút @return action link */
    public static CopilotAction link(String route, String label) {
        return new CopilotAction("link", route, label, null);
    }

    /**
     * Tạo action dạng nút link kèm dữ liệu biểu đồ tròn.
     *
     * @param route đường dẫn @param label nhãn nút @param chart dữ liệu biểu đồ @return action link
     */
    public static CopilotAction linkWithChart(String route, String label, CopilotChartData chart) {
        return new CopilotAction("link", route, label, chart);
    }
}
