package vn.com.be_crm.infrastructure.shared.util;

import org.springframework.security.core.Authentication;

/** Utility helper cho Spring Security context. */
public class SecurityUtils {
    private SecurityUtils() {}

    /**
     * Kiểm tra người dùng hiện tại có quyền admin (USER_MANAGE) không.
     * @param auth Spring Security Authentication
     * @return true nếu có quyền USER_MANAGE
     */
    public static boolean isAdmin(Authentication auth) {
        if (auth == null) return false;
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().contains("USER_MANAGE"));
    }
}
