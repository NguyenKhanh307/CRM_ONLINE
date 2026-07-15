package vn.com.be_crm.infrastructure.shared.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import vn.com.be_crm.application.shared.security.IGoogleTokenVerifier;
import vn.com.be_crm.domain.shared.exception.DomainException;

import java.time.Duration;
import java.util.Map;

/**
 * Xác thực Google ID token qua endpoint tokeninfo của Google — không cần thư viện ngoài,
 * dùng RestClient của spring-web (cùng cách với GeminiAiServiceImpl).
 */
@Component
public class GoogleTokenVerifierImpl implements IGoogleTokenVerifier {

    private static final Logger log = LoggerFactory.getLogger(GoogleTokenVerifierImpl.class);

    /** OAuth Client ID (audience hợp lệ của ID token) — cấu hình app.google.client-id. */
    private final String clientId;
    private final RestClient restClient;

    /**
     * @param clientId OAuth Client ID Google (app.google.client-id)
     */
    public GoogleTokenVerifierImpl(@Value("${app.google.client-id:}") String clientId) {
        this.clientId = clientId;
        var factory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
        factory.setConnectTimeout((int) Duration.ofSeconds(10).toMillis());
        factory.setReadTimeout((int) Duration.ofSeconds(15).toMillis());
        this.restClient = RestClient.builder()
                .baseUrl("https://oauth2.googleapis.com")
                .requestFactory(factory)
                .build();
    }

    /**
     * Gọi tokeninfo để xác thực chữ ký + hạn token, kiểm tra audience và email_verified.
     *
     * @param idToken JWT ID token từ FE
     * @return thông tin người dùng Google
     * @throws DomainException nếu chưa cấu hình client-id, token sai/hết hạn, hoặc sai audience
     */
    @Override
    public GoogleUserInfo verify(String idToken) {
        if (clientId == null || clientId.isBlank()) {
            throw new DomainException("Chưa cấu hình Google Client ID (app.google.client-id).");
        }
        if (idToken == null || idToken.isBlank()) {
            throw new DomainException("Thiếu Google ID token.");
        }

        Map<String, Object> res;
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> body = restClient.get()
                    .uri("/tokeninfo?id_token={token}", idToken)
                    .retrieve()
                    .body(Map.class);
            res = body;
        } catch (RestClientResponseException e) {
            log.warn("Google tokeninfo trả lỗi HTTP {}: {}", e.getStatusCode().value(), e.getResponseBodyAsString());
            throw new DomainException("Xác thực Google thất bại — token không hợp lệ hoặc đã hết hạn.");
        } catch (Exception e) {
            log.warn("Lỗi khi gọi Google tokeninfo: {}", e.toString());
            throw new DomainException("Không kết nối được tới Google để xác thực. Vui lòng thử lại sau.");
        }

        if (res == null) {
            throw new DomainException("Xác thực Google thất bại — phản hồi rỗng.");
        }

        String aud = asString(res.get("aud"));
        if (!clientId.equals(aud)) {
            log.warn("Google ID token sai audience: mong đợi {}, nhận {}", clientId, aud);
            throw new DomainException("Xác thực Google thất bại — token không dành cho ứng dụng này.");
        }

        boolean emailVerified = "true".equalsIgnoreCase(asString(res.get("email_verified")));
        String email = asString(res.get("email"));
        if (email == null || email.isBlank() || !emailVerified) {
            throw new DomainException("Tài khoản Google chưa xác minh email.");
        }

        return new GoogleUserInfo(email, asString(res.get("name")), asString(res.get("picture")), true);
    }

    /** Ép giá trị JSON về String (tokeninfo trả các trường dưới dạng chuỗi). */
    private String asString(Object v) {
        return v == null ? null : v.toString();
    }
}
