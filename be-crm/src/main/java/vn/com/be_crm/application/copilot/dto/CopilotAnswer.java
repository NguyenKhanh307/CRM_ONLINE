package vn.com.be_crm.application.copilot.dto;

/**
 * Kết quả trả lời của trợ lý AI Copilot.
 *
 * @param answer văn bản trả lời (tiếng Việt) do mô hình sinh ra dựa trên dữ liệu CRM
 * @param action hành động đính kèm cho FE (điều hướng/link) — null nếu chỉ trả lời text
 */
public record CopilotAnswer(String answer, CopilotAction action) {

    /** Tạo câu trả lời chỉ có text (không hành động). @param answer văn bản trả lời */
    public CopilotAnswer(String answer) {
        this(answer, null);
    }
}
