package vn.com.be_crm.infrastructure.shared.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import vn.com.be_crm.infrastructure.shared.security.JwtAuthFilter;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Cấu hình Spring Security với JWT stateless — permit /api/auth/login, require auth cho các route còn lại.
 * Phân quyền: URL rules cho vùng quản trị (ADMIN) + method security (@PreAuthorize theo permission code
 * module.action đọc từ JWT claim) cho các endpoint side-effect.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    /** Danh sách origin frontend được phép (phân tách bằng dấu phẩy) — cấu hình app.cors.allowed-origins. */
    @Value("${app.cors.allowed-origins:http://localhost:5173}")
    private String allowedOrigins;

    /**
     * @param jwtAuthFilter filter xác thực JWT
     */
    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    /**
     * Cấu hình Security filter chain: stateless JWT, permit login endpoint.
     *
     * @param http HttpSecurity builder
     * @return SecurityFilterChain đã cấu hình
     * @throws Exception nếu cấu hình thất bại
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login", "/api/auth/google", "/api/auth/activate", "/api/tracking/**", "/api/public/**").permitAll()
                        // Vùng quản trị: chỉ ADMIN được đăng ký NV + thao tác mutating trên users/roles/permissions.
                        // GET vẫn mở cho mọi user đã đăng nhập (FE dùng làm lookup: HandoverModal, form thêm mới...).
                        .requestMatchers("/api/auth/register-employee").hasAuthority("ADMIN")
                        .requestMatchers("/api/handover/all").hasAnyAuthority("ADMIN", "SALES_MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/users/**", "/api/roles/**", "/api/permissions/**").authenticated()
                        .requestMatchers("/api/users/**", "/api/roles/**", "/api/permissions/**").hasAuthority("ADMIN")
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, authEx) ->
                                res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized"))
                        // 403 trả JSON khớp format ApiResponse để FE đọc message qua error.response.data.message
                        .accessDeniedHandler((req, res, deniedEx) -> {
                            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            res.setContentType("application/json");
                            res.setCharacterEncoding(StandardCharsets.UTF_8.name());
                            res.getWriter().write("{\"message\":\"Bạn không có quyền thực hiện thao tác này\",\"status\":403}");
                        })
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * Cho phép các frontend origin (cấu hình app.cors.allowed-origins) gọi API với Authorization header.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(allowedOrigins.split(",")));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /**
     * Ngăn Spring Boot tự đăng ký JwtAuthFilter vào Servlet filter chain.
     * Filter chỉ chạy trong Security filter chain (qua addFilterBefore).
     *
     * @param filter JwtAuthFilter bean
     * @return FilterRegistrationBean bị tắt
     */
    @Bean
    public FilterRegistrationBean<JwtAuthFilter> jwtFilterRegistration(JwtAuthFilter filter) {
        FilterRegistrationBean<JwtAuthFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }
}
