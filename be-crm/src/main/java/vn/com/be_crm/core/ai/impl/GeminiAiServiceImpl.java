package vn.com.be_crm.core.ai.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import vn.com.be_crm.core.ai.port.IAiService;
import vn.com.be_crm.core.error.frontend.DomainException;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Gọi Google Gemini REST API (generateContent) để sinh văn bản.
 * Cấu hình qua app.ai.* — không cần thư viện ngoài, dùng RestClient của spring-web.
 */
@Component
public class GeminiAiServiceImpl implements IAiService {

    private static final Logger log = LoggerFactory.getLogger(GeminiAiServiceImpl.class);

    /** API key lấy từ Google AI Studio — cấu hình app.ai.api-key. */
    private final String apiKey;
    /** Tên model (vd gemini-flash-latest) — cấu hình app.ai.model. */
    private final String model;
    private final RestClient restClient;

    /**
     * @param apiKey  API key Gemini (app.ai.api-key)
     * @param model   tên model (app.ai.model)
     * @param baseUrl base URL Gemini (app.ai.base-url)
     */
    public GeminiAiServiceImpl(@Value("${app.ai.api-key}") String apiKey,
                               @Value("${app.ai.model}") String model,
                               @Value("${app.ai.base-url}") String baseUrl) {
        this.apiKey = apiKey;
        this.model = model;
        var factory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
        factory.setConnectTimeout((int) Duration.ofSeconds(10).toMillis());
        factory.setReadTimeout((int) Duration.ofSeconds(60).toMillis());
        this.restClient = RestClient.builder().baseUrl(baseUrl).requestFactory(factory).build();
    }

    /**
     * Gửi systemPrompt + userPrompt tới Gemini và trả về văn bản trả lời.
     *
     * @param systemPrompt chỉ dẫn hệ thống (có thể null)
     * @param userPrompt   nội dung câu hỏi + ngữ cảnh
     * @return văn bản mô hình sinh ra
     */
    @Override
    public String generate(String systemPrompt, String userPrompt) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new DomainException("Chưa cấu hình khóa API cho trợ lý AI (app.ai.api-key).");
        }
        Map<String, Object> body = buildRequestBody(systemPrompt, userPrompt);
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> res = restClient.post()
                    .uri("/v1beta/models/{model}:generateContent?key={key}", model, apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(Map.class);
            return extractText(res);
        } catch (DomainException e) {
            throw e;
        } catch (RestClientResponseException e) {
            int status = e.getStatusCode().value();
            String errBody = e.getResponseBodyAsString();
            log.warn("Gemini trả lỗi HTTP {} (model={}): {}", status, model, errBody);
            throw new DomainException(mapHttpError(status, errBody));
        } catch (Exception e) {
            log.warn("Lỗi khi gọi Gemini (model={}): {}", model, e.toString());
            throw new DomainException("Không gọi được trợ lý AI (lỗi mạng/timeout). Vui lòng thử lại sau.", e);
        }
    }

    /** Ánh xạ mã HTTP lỗi của Gemini sang message dễ hiểu (kèm trích message gốc của Google). */
    private String mapHttpError(int status, String errBody) {
        String detail = extractGoogleMessage(errBody);
        return switch (status) {
            case 400 -> "Yêu cầu tới Gemini không hợp lệ — thường do sai tên model (app.ai.model=" + model
                    + ") hoặc body. Chi tiết: " + detail;
            case 401, 403 -> "Khóa API Gemini không hợp lệ hoặc chưa được cấp quyền. Chi tiết: " + detail;
            case 404 -> "Không tìm thấy model '" + model + "' — đổi app.ai.model (vd gemini-flash-latest). Chi tiết: " + detail;
            case 429 -> "Đã vượt hạn mức Gemini (free tier) — thử lại sau. Chi tiết: " + detail;
            default -> "Gemini trả lỗi HTTP " + status + ". Chi tiết: " + detail;
        };
    }

    /** Lấy trường message trong JSON lỗi của Google (nếu có) để hiển thị gọn. */
    private String extractGoogleMessage(String body) {
        if (body == null || body.isBlank()) return "(không có nội dung)";
        int i = body.indexOf("\"message\"");
        if (i < 0) return body.length() > 300 ? body.substring(0, 300) : body;
        int start = body.indexOf(':', i);
        int q1 = body.indexOf('"', start + 1);
        int q2 = q1 < 0 ? -1 : body.indexOf('"', q1 + 1);
        return (q1 >= 0 && q2 > q1) ? body.substring(q1 + 1, q2) : body.substring(0, Math.min(300, body.length()));
    }

    /** Dựng request body theo định dạng generateContent của Gemini. */
    private Map<String, Object> buildRequestBody(String systemPrompt, String userPrompt) {
        Map<String, Object> content = Map.of("parts", List.of(Map.of("text", userPrompt == null ? "" : userPrompt)));
        Map<String, Object> body = new java.util.HashMap<>();
        body.put("contents", List.of(content));
        body.put("generationConfig", Map.of("temperature", 0.3));
        if (systemPrompt != null && !systemPrompt.isBlank()) {
            body.put("systemInstruction", Map.of("parts", List.of(Map.of("text", systemPrompt))));
        }
        return body;
    }

    /** Trích text từ candidates[0].content.parts[0].text; ném lỗi nếu phản hồi rỗng/bị chặn. */
    @SuppressWarnings("unchecked")
    private String extractText(Map<String, Object> res) {
        try {
            List<Object> candidates = (List<Object>) res.get("candidates");
            Map<String, Object> content = (Map<String, Object>) ((Map<String, Object>) candidates.get(0)).get("content");
            List<Object> parts = (List<Object>) content.get("parts");
            Object text = ((Map<String, Object>) parts.get(0)).get("text");
            if (text == null || text.toString().isBlank()) throw new DomainException("empty");
            return text.toString().trim();
        } catch (DomainException e) {
            throw e;
        } catch (Exception e) {
            throw new DomainException("Trợ lý AI không trả về nội dung (có thể do bị chặn hoặc hết hạn mức).");
        }
    }
}
