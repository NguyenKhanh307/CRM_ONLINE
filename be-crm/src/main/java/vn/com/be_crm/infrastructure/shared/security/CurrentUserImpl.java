package vn.com.be_crm.infrastructure.shared.security;

import org.springframework.stereotype.Component;
import vn.com.be_crm.application.shared.security.ICurrentUser;
import vn.com.be_crm.infrastructure.shared.audit.CurrentUserHolder;

/**
 * Đọc ID người dùng hiện tại từ ThreadLocal đã được {@code JwtAuthFilter} set sẵn
 * (dùng chung với cơ chế đóng dấu created_by/updated_by).
 */
@Component
public class CurrentUserImpl implements ICurrentUser {

    /** {@inheritDoc} */
    @Override
    public Long id() {
        return CurrentUserHolder.get();
    }
}
