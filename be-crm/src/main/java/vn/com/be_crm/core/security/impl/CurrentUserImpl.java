package vn.com.be_crm.core.security.impl;

import org.springframework.stereotype.Component;
import vn.com.be_crm.core.security.port.ICurrentUser;
import vn.com.be_crm.core.audit.CurrentUserHolder;

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
