package vn.com.be_crm.core.util;

import org.springframework.security.core.Authentication;

/** Utility helper cho Spring Security context. */
public class SecurityUtils {
    private SecurityUtils() {}

    /**
     * Kiểm tra người dùng hiện tại có role ADMIN không.
     * @param auth Spring Security Authentication
     * @return true nếu có role ADMIN
     */
    public static boolean isAdmin(Authentication auth) {
        if (auth == null) return false;
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN"));
    }

    /**
     * Kiểm tra người dùng có role ADMIN hoặc SALES_MANAGER — có quyền bàn giao bất kỳ bản ghi nào.
     * @param auth Spring Security Authentication
     * @return true nếu là ADMIN hoặc SALES_MANAGER
     */
    public static boolean isAdminOrManager(Authentication auth) {
        if (auth == null) return false;
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN") || a.getAuthority().equals("SALES_MANAGER"));
    }
}
