package vn.com.be_crm.presentation.copilot.request;

import jakarta.validation.constraints.NotBlank;

/**
 * HTTP body cho endpoint hỏi trợ lý AI.
 *
 * @param question câu hỏi của người dùng (bắt buộc)
 */
public record AskCopilotRequest(@NotBlank(message = "Vui lòng nhập câu hỏi") String question) {
}
