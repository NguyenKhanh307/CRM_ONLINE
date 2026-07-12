package vn.com.be_crm.infrastructure.shared.notify;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Component;
import vn.com.be_crm.application.shared.notify.IManagerResolver;
import vn.com.be_crm.domain.auth.repository.IUserRoleRepository;
import vn.com.be_crm.infrastructure.shared.tx.TxSupport;

import java.util.List;

/**
 * Xác định quản lý của một nhân viên qua cây đơn vị: users.unit_id → org_units.manager_id,
 * leo dần lên org_units.parent_id nếu đơn vị chưa gán trưởng.
 * Không tìm được → fallback về toàn bộ SALES_MANAGER (thông báo không bị rơi mất).
 */
@Component
public class ManagerResolverImpl implements IManagerResolver {

    /** Chặn vòng lặp vô tận nếu cây đơn vị bị cấu hình sai (A là cha của B, B là cha của A). */
    private static final int MAX_DEPTH = 10;

    /** Vai trò dùng làm phương án dự phòng khi không đơn vị nào có trưởng. */
    private static final List<String> FALLBACK_ROLES = List.of("SALES_MANAGER");

    private final SessionFactory sf;
    private final IUserRoleRepository userRoleRepo;

    /** @param sf Hibernate SessionFactory @param userRoleRepo port tra user theo vai trò */
    public ManagerResolverImpl(SessionFactory sf, IUserRoleRepository userRoleRepo) {
        this.sf = sf;
        this.userRoleRepo = userRoleRepo;
    }

    /** {@inheritDoc} */
    @Override
    public List<Long> managersOf(Long userId) {
        if (userId == null) return userRoleRepo.findUserIdsByRoleCodes(FALLBACK_ROLES);

        Long managerId = TxSupport.read(sf, s -> {
            Object unit = s.createNativeQuery("SELECT unit_id FROM users WHERE id = :id", Object.class)
                    .setParameter("id", userId).uniqueResult();
            Long unitId = unit == null ? null : ((Number) unit).longValue();

            for (int depth = 0; unitId != null && depth < MAX_DEPTH; depth++) {
                Object[] row = (Object[]) s.createNativeQuery(
                                "SELECT manager_id, parent_id FROM org_units WHERE id = :id", Object[].class)
                        .setParameter("id", unitId).uniqueResult();
                if (row == null) return null;
                if (row[0] != null) return ((Number) row[0]).longValue();
                unitId = row[1] == null ? null : ((Number) row[1]).longValue();
            }
            return null;
        });

        // Không tự thông báo cho chính mình (owner đã được thêm riêng ở nơi gọi)
        if (managerId != null && !managerId.equals(userId)) return List.of(managerId);
        if (managerId != null) return List.of();
        return userRoleRepo.findUserIdsByRoleCodes(FALLBACK_ROLES).stream()
                .filter(id -> !id.equals(userId)).toList();
    }
}
