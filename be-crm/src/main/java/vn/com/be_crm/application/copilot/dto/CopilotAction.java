package vn.com.be_crm.application.copilot.dto;

/**
 * Hành động đính kèm câu trả lời của trợ lý AI để frontend thực thi.
 *
 * @param type  "navigate" = FE tự điều hướng ngay; "link" = FE hiện nút, bấm mới điều hướng
 * @param route đường dẫn nội bộ FE (vd "/khach-hang", "/phan-tich?period=quarter")
 * @param label nhãn hiển thị cho nút (dùng khi type = "link")
 */
public record CopilotAction(String type, String route, String label) {

    /** Tạo action điều hướng ngay (FE tự nhảy trang). @param route đường dẫn @return action navigate */
    public static CopilotAction navigate(String route) {
        return new CopilotAction("navigate", route, null);
    }

    /** Tạo action dạng nút link (bấm mới mở). @param route đường dẫn @param label nhãn nút @return action link */
    public static CopilotAction link(String route, String label) {
        return new CopilotAction("link", route, label);
    }
}
