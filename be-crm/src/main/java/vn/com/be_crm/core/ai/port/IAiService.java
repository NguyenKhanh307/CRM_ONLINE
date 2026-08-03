package vn.com.be_crm.core.ai.port;

/**
 * Port sinh văn bản bằng mô hình ngôn ngữ (LLM) — application layer không biết nhà cung cấp cụ thể
 * (Gemini/Claude/OpenAI). Đổi nhà cung cấp chỉ cần thay implementation ở infrastructure layer.
 */
public interface IAiService {

    /**
     * Sinh văn bản trả lời từ chỉ dẫn hệ thống và nội dung người dùng.
     *
     * @param systemPrompt chỉ dẫn vai trò/ràng buộc cho mô hình (có thể null)
     * @param userPrompt   nội dung câu hỏi kèm ngữ cảnh dữ liệu
     * @return văn bản mô hình sinh ra
     */
    String generate(String systemPrompt, String userPrompt);
}
