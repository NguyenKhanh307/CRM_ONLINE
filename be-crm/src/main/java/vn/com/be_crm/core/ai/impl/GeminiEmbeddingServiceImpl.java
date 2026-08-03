package vn.com.be_crm.core.ai.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import vn.com.be_crm.core.ai.port.IEmbeddingService;
import vn.com.be_crm.core.error.frontend.DomainException;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Gọi Google Gemini REST API (embedContent) để nhúng câu hỏi thành vector.
 * Cùng khuôn với {@link GeminiAiServiceImpl} — dùng RestClient của spring-web,
 * không thêm thư viện.
 * <p>
 * Chỉ chạy 1 lần cho mỗi câu hỏi của người dùng (vài chục request/ngày), nên
 * không bao giờ
 * chạm trần hạn mức. Phần tốn hạn mức là đánh chỉ mục dữ liệu — nằm ở
 * {@code tools/indexer/}.
 */
@Component
public class GeminiEmbeddingServiceImpl implements IEmbeddingService {

    private static final Logger log = LoggerFactory.getLogger(GeminiEmbeddingServiceImpl.class);

    /** Nhúng câu TRUY VẤN (khác RETRIEVAL_DOCUMENT mà indexer dùng cho dữ liệu). */
    private static final String TASK_TYPE = "RETRIEVAL_QUERY";

    private final String apiKey;
    private final String model;
    private final int dimensions;
    private final RestClient restClient;

    /**
     * @param apiKey     API key Gemini (app.ai.api-key — dùng chung với Copilot)
     * @param model      tên model nhúng (app.ai.embed.model)
     * @param dimensions số chiều vector (app.ai.embed.dimensions)
     * @param baseUrl    base URL Gemini (app.ai.base-url)
     */
    public GeminiEmbeddingServiceImpl(@Value("${app.ai.api-key}") String apiKey,
            @Value("${app.ai.embed.model}") String model,
            @Value("${app.ai.embed.dimensions}") int dimensions,
            @Value("${app.ai.base-url}") String baseUrl) {
        this.apiKey = apiKey;
        this.model = model;
        this.dimensions = dimensions;
        var factory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
        factory.setConnectTimeout((int) Duration.ofSeconds(10).toMillis());
        factory.setReadTimeout((int) Duration.ofSeconds(30).toMillis());
        this.restClient = RestClient.builder().baseUrl(baseUrl).requestFactory(factory).build();
    }

    /** {@inheritDoc} */
    @Override
    // Chú ý: số chiều embedding phải trùng với cấu hình indexer
    // (tools/indexer/embed.py) — nếu lệch thì mọi kết quả tìm kiếm sẽ sai.
    public int dimensions() {
        return dimensions;
    }

    /** {@inheritDoc} */
    @Override
    public float[] embed(String text) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new DomainException("Chưa cấu hình khóa API cho trợ lý AI (app.ai.api-key).");
        }
        Map<String, Object> body = Map.of(
                "content", Map.of("parts", List.of(Map.of("text", text == null ? "" : text))),
                "taskType", TASK_TYPE,
                "outputDimensionality", dimensions);
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> res = restClient.post()
                    .uri("/v1beta/models/{model}:embedContent?key={key}", model, apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(Map.class);
            return normalize(extractValues(res));
        } catch (DomainException e) {
            throw e;
        } catch (RestClientResponseException e) {
            log.warn("Gemini embed trả lỗi HTTP {} (model={}): {}",
                    e.getStatusCode().value(), model, e.getResponseBodyAsString());
            throw new DomainException("Không nhúng được câu hỏi (lỗi gọi Gemini embedding).");
        } catch (Exception e) {
            log.warn("Lỗi khi gọi Gemini embed (model={}): {}", model, e.toString());
            throw new DomainException("Không nhúng được câu hỏi (lỗi mạng/timeout).");
        }
    }

    /** Trích embedding.values từ phản hồi; ném lỗi nếu rỗng hoặc sai số chiều. */
    @SuppressWarnings("unchecked")
    private float[] extractValues(Map<String, Object> res) {
        List<Number> values;
        try {
            values = (List<Number>) ((Map<String, Object>) res.get("embedding")).get("values");
        } catch (Exception e) {
            throw new DomainException("Gemini embedding trả về nội dung không đọc được.");
        }
        if (values == null || values.isEmpty()) {
            throw new DomainException("Gemini embedding trả về vector rỗng.");
        }
        // Số chiều lệch nghĩa là cấu hình backend khác indexer -> mọi kết quả tìm kiếm
        // sẽ sai.
        if (values.size() != dimensions) {
            throw new DomainException("Gemini trả vector " + values.size() + " chiều nhưng cấu hình là "
                    + dimensions + " (app.ai.embed.dimensions) — kiểm tra lại cấu hình.");
        }
        float[] out = new float[values.size()];
        for (int i = 0; i < out.length; i++)
            out[i] = values.get(i).floatValue();
        return out;
    }

    /**
     * Chuẩn hóa L2 (độ dài vector = 1).
     * <p>
     * 🚨 BẮT BUỘC: {@code gemini-embedding-001} chỉ tự chuẩn hóa ở 3072 chiều; dùng
     * {@code outputDimensionality} nhỏ hơn thì Google yêu cầu client tự normalize.
     * Công thức này phải giống hệt {@code tools/indexer/embed.py::normalize} — lệch
     * nhau là
     * vector câu hỏi và vector dữ liệu không so sánh được, kết quả sai âm thầm.
     *
     * @param vec vector thô
     * @return vector đã chuẩn hóa (trả nguyên nếu độ dài bằng 0)
     */
    private float[] normalize(float[] vec) {
        double sum = 0;
        for (float v : vec)
            sum += (double) v * v;
        double norm = Math.sqrt(sum);
        if (norm == 0)
            return vec;
        for (int i = 0; i < vec.length; i++)
            vec[i] = (float) (vec[i] / norm);
        return vec;
    }
}
