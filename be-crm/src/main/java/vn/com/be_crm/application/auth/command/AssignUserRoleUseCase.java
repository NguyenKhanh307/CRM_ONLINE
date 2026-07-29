package vn.com.be_crm.application.auth.command;

import vn.com.be_crm.application.auth.dto.AssignUserRoleCommand;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.auth.entity.UserRole;
import vn.com.be_crm.domain.auth.repository.IUserRoleRepository;

/**
 * Use case gán vai trò cho người dùng.
 */
public class AssignUserRoleUseCase implements IUseCase<AssignUserRoleCommand, Void> {

    private final IUserRoleRepository repository;

    /** @param repository port lưu trữ UserRole */
    public AssignUserRoleUseCase(IUserRoleRepository repository) {
        this.repository = repository;
    }

    /**
     * Tạo liên kết user-role và lưu vào DB. Mỗi người chỉ thuộc một nhóm quyền — xóa mọi vai trò
     * hiện có của người dùng trước khi gán vai trò mới (kể cả khi gọi thẳng API, không chỉ qua FE).
     *
     * @param command userId và roleId cần gán
     * @return null
     */
    @Override
    public Void execute(AssignUserRoleCommand command) {
        repository.deleteByUserId(command.getUserId());
        UserRole ur = UserRole.builder()
                .userId(command.getUserId())
                .roleId(command.getRoleId())
                .build();
        repository.save(ur);
        return null;
    }
}
