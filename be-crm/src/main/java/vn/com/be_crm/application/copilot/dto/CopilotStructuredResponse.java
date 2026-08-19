package vn.com.be_crm.application.copilot.dto;

// phản hồi JSON có cấu trúc từ LLM (Gemini structured output). "queryable=true" nghĩa là câu hỏi
// ánh xạ được vào một truy vấn có cấu trúc (spec) mà Java tự chạy an toàn + tự tính số thật —
// "answer" của LLM khi đó chỉ dùng làm câu dẫn phụ, KHÔNG dùng cho số liệu. "queryable=false" thì
// dùng thẳng "answer" — y hệt hành vi RAG tự do trước đây.
public record CopilotStructuredResponse(
        boolean queryable,
        String queryType,
        CopilotQuerySpec spec,
        String answer) {
}
