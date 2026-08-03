package vn.com.be_crm.core.security.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import vn.com.be_crm.core.security.port.ITokenProvider;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

/**
 * Tạo và xác thực JWT token dùng JJWT 0.12.x với thuật toán HS256.
 */
@Component
public class JwtProvider implements ITokenProvider {

    private final SecretKey secretKey;
    private final long expirationMs;

    /**
     * @param secret       chuỗi bí mật từ application.properties
     * @param expirationMs thời gian sống token (ms)
     */
    public JwtProvider(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-ms:86400000}") long expirationMs) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    /**
     * Tạo JWT token chứa userId, email (subject), roles, permissions, dataAccessFromYear.
     *
     * @param userId             ID người dùng
     * @param email              email (subject)
     * @param roles              danh sách code vai trò
     * @param permissions        danh sách code quyền (module.action)
     * @param dataAccessFromYear năm sớm nhất được xem data (null = không giới hạn)
     * @return JWT token dạng chuỗi
     */
    @Override
    public String generateToken(Long userId, String email, List<String> roles, List<String> permissions, Integer dataAccessFromYear) {
        var builder = Jwts.builder()
                .subject(email)
                .claim("userId", userId)
                .claim("roles", roles)
                .claim("permissions", permissions);
        if (dataAccessFromYear != null) {
            builder.claim("dataAccessFromYear", dataAccessFromYear);
        }
        return builder
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(secretKey)
                .compact();
    }

    /**
     * Kiểm tra token có hợp lệ không.
     *
     * @param token JWT token
     * @return true nếu hợp lệ và chưa hết hạn
     */
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Lấy email (subject) từ token.
     *
     * @param token JWT token
     * @return email
     */
    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * Lấy danh sách roles từ token claims.
     *
     * @param token JWT token
     * @return danh sách code vai trò
     */
    public List<String> extractRoles(String token) {
        Object rolesObj = getClaims(token).get("roles");
        if (rolesObj instanceof List<?> list) {
            return list.stream().map(Object::toString).toList();
        }
        return List.of();
    }

    /**
     * Lấy danh sách permission codes (module.action) từ token claims.
     *
     * @param token JWT token
     * @return danh sách code quyền, rỗng nếu token cũ không có claim
     */
    public List<String> extractPermissions(String token) {
        Object permsObj = getClaims(token).get("permissions");
        if (permsObj instanceof List<?> list) {
            return list.stream().map(Object::toString).toList();
        }
        return List.of();
    }

    /**
     * Lấy dataAccessFromYear từ token claims.
     *
     * @param token JWT token
     * @return năm sớm nhất được xem data, hoặc null nếu không giới hạn
     */
    public Integer extractDataAccessFromYear(String token) {
        Object val = getClaims(token).get("dataAccessFromYear");
        if (val instanceof Number n) return n.intValue();
        return null;
    }

    /**
     * Lấy userId từ token claims.
     * @param token JWT token
     * @return userId, hoặc null nếu không có
     */
    public Long extractUserId(String token) {
        Object val = getClaims(token).get("userId");
        if (val instanceof Number n) return n.longValue();
        return null;
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
