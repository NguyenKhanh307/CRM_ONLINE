package vn.com.be_crm.infrastructure.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Cấu hình Spring Security — tạm thời cho phép toàn bộ request (JWT sẽ bổ sung sau).
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Cho phép tất cả request không cần xác thực trong giai đoạn phát triển.
     *
     * @param http HttpSecurity builder
     * @return SecurityFilterChain đã cấu hình
     * @throws Exception nếu cấu hình thất bại
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }
}
