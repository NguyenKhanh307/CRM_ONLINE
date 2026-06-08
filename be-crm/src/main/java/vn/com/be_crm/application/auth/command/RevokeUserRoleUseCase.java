package vn.com.be_crm.application.auth.command;

import vn.com.be_crm.application.auth.dto.AssignUserRoleCommand;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.auth.repository.IUserRoleRepository;

/**
 * Use case thu hồi vai trò khỏi người dùng.
 */
public class RevokeUserRoleUseCase implements IUseCase<AssignUserRoleCommand, Void> {

    private final IUserRoleRepository repository;

    /** @param repository port lưu trữ UserRole */
    public RevokeUserRoleUseCase(IUserRoleRepository repository) {
        this.repository = repository;
    }

    /**
     * Xóa liên kết user-role khỏi DB.
     *
     * @param command userId và roleId cần thu hồi
     * @return null
     */
    @Override
    public Void execute(AssignUserRoleCommand command) {
        repository.deleteByUserIdAndRoleId(command.getUserId(), command.getRoleId());
        return null;
    }
}
