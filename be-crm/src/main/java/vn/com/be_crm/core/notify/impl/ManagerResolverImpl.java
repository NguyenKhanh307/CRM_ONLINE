package vn.com.be_crm.core.notify.impl;

import org.springframework.stereotype.Component;
import vn.com.be_crm.core.notify.port.IManagerResolver;
import vn.com.be_crm.domain.auth.repository.IUserRoleRepository;

import java.util.List;

/**
 * Xác định quản lý của một nhân viên theo VAI TRÒ: toàn bộ người mang role SALES_MANAGER.
 * <p>CRM phục vụ công ty nhỏ nên không còn cây đơn vị (bảng {@code org_units} đã gỡ) — một nhóm
 * quản lý duy nhất là đủ. Trước đây lớp này leo {@code users.unit_id → org_units.manager_id →
 * parent_id} rồi mới rơi về đúng nhánh này khi không đơn vị nào gán trưởng.
 */
@Component
public class ManagerResolverImpl implements IManagerResolver {

    /** Vai trò được xem là quản lý trực tiếp của nhân viên kinh doanh. */
    private static final List<String> MANAGER_ROLES = List.of("SALES_MANAGER");

    private final IUserRoleRepository userRoleRepo;

    /** @param userRoleRepo port tra user theo vai trò */
    public ManagerResolverImpl(IUserRoleRepository userRoleRepo) {
        this.userRoleRepo = userRoleRepo;
    }

    /** {@inheritDoc} */
    @Override
    public List<Long> managersOf(Long userId) {
        List<Long> managers = userRoleRepo.findUserIdsByRoleCodes(MANAGER_ROLES);
        if (userId == null) return managers;
        // Không tự thông báo cho chính mình (owner đã được thêm riêng ở nơi gọi)
        return managers.stream().filter(id -> !id.equals(userId)).toList();
    }
}
