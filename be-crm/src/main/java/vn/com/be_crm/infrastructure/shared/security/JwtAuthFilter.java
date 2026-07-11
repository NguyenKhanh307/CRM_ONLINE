package vn.com.be_crm.infrastructure.shared.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Filter xác thực JWT trên mỗi request — không cần DB lookup,
 * dùng claims trong token để set SecurityContext.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    /**
     * @param jwtProvider bean xử lý JWT
     */
    public JwtAuthFilter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    /**
     * Trích xuất Bearer token, validate và set Authentication vào SecurityContext.
     *
     * @param request  HTTP request
     * @param response HTTP response
     * @param chain    filter chain
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            if (jwtProvider.validateToken(token)) {
                String email = jwtProvider.extractEmail(token);
                List<String> roles = jwtProvider.extractRoles(token);
                List<String> permissions = jwtProvider.extractPermissions(token);
                Integer dataAccessFromYear = jwtProvider.extractDataAccessFromYear(token);
                // Authorities = role codes + permission codes (module.action) — dùng cho hasAuthority(...)
                var authorities = java.util.stream.Stream.concat(roles.stream(), permissions.stream())
                        .map(SimpleGrantedAuthority::new)
                        .toList();
                var auth = new UsernamePasswordAuthenticationToken(email, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(auth);
                if (dataAccessFromYear != null) {
                    request.setAttribute("dataAccessFromYear", dataAccessFromYear);
                }
                Long userId = jwtProvider.extractUserId(token);
                request.setAttribute("userId", userId);
            }
        }
        chain.doFilter(request, response);
    }
}
