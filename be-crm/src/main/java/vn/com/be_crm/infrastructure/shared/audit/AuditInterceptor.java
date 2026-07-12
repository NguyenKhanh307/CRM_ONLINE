package vn.com.be_crm.infrastructure.shared.audit;

import org.hibernate.Interceptor;
import org.hibernate.type.Type;

import java.util.Objects;

/**
 * Đóng dấu {@code created_by} / {@code updated_by} tự động cho mọi {@link IAuditable} entity,
 * bắt tại tầng Hibernate nên KHÔNG phải sửa 68 use case Create/Update.
 *
 * <ul>
 *   <li>{@code onPersist} (INSERT, kể cả qua {@code session.merge} một entity mới) → ghi người tạo.</li>
 *   <li>{@code onFlushDirty} (UPDATE) → ghi người sửa, đồng thời <b>khôi phục</b> {@code created_by}
 *       từ {@code previousState}: mapper dựng entity mới với {@code createdBy = null} nên nếu không
 *       khôi phục, giá trị trong DB sẽ bị NULL đè.</li>
 * </ul>
 *
 * <p>Trả về {@code true} → Hibernate tự gọi {@code persister.setValues(entity, state)}, nên chỉ cần
 * sửa mảng {@code state} là đủ (không dính bẫy "sửa state mà quên sửa property" của EventListener).
 *
 * <p><b>Stateless</b> — interceptor ở scope SessionFactory nên dùng chung cho mọi thread;
 * mọi trạng thái đọc từ {@link CurrentUserHolder} (ThreadLocal).
 */
public final class AuditInterceptor implements Interceptor {

    private static final String CREATED_BY = "createdBy";
    private static final String UPDATED_BY = "updatedBy";

    /** {@inheritDoc} */
    @Override
    public boolean onPersist(Object entity, Object id, Object[] state, String[] propertyNames, Type[] types) {
        if (!(entity instanceof IAuditable)) return false;
        Long userId = CurrentUserHolder.get();
        // Không có người dùng (web tracking công khai) → để NULL: bản ghi do khách ẩn danh tạo
        if (userId == null) return false;

        boolean changed = false;
        int c = indexOf(propertyNames, CREATED_BY);
        if (c >= 0 && state[c] == null) {
            state[c] = userId;
            changed = true;
        }
        int u = indexOf(propertyNames, UPDATED_BY);
        if (u >= 0 && state[u] == null) {
            state[u] = userId;
            changed = true;
        }
        return changed;
    }

    /** {@inheritDoc} */
    @Override
    public boolean onFlushDirty(Object entity, Object id, Object[] currentState, Object[] previousState,
                                String[] propertyNames, Type[] types) {
        if (!(entity instanceof IAuditable)) return false;
        Long userId = CurrentUserHolder.get();
        boolean changed = false;

        // created_by: luôn giữ giá trị đang có trong DB (mapper dựng entity mới nên currentState = null)
        int c = indexOf(propertyNames, CREATED_BY);
        if (c >= 0 && !Objects.equals(currentState[c], previousState[c])) {
            currentState[c] = previousState[c];
            changed = true;
        }

        // updated_by: người đang thao tác; không có user thì giữ nguyên giá trị cũ (không NULL hóa)
        int u = indexOf(propertyNames, UPDATED_BY);
        if (u >= 0) {
            Object target = userId != null ? userId : previousState[u];
            if (!Objects.equals(currentState[u], target)) {
                currentState[u] = target;
                changed = true;
            }
        }
        return changed;
    }

    /** Vị trí của property trong mảng propertyNames, -1 nếu entity không có property đó. */
    private static int indexOf(String[] propertyNames, String name) {
        for (int i = 0; i < propertyNames.length; i++) {
            if (name.equals(propertyNames[i])) return i;
        }
        return -1;
    }
}
