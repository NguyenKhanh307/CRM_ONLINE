package vn.com.be_crm.application.copilot.dto;

/**
 * Kết quả trả lời của trợ lý AI Copilot.
 *
 * @param answer văn bản trả lời (tiếng Việt) do mô hình sinh ra dựa trên dữ liệu CRM
 */
public record CopilotAnswer(String answer) {
}
