package vn.com.be_crm.application.auth.command;

import vn.com.be_crm.application.auth.dto.RoleResult;
import vn.com.be_crm.application.auth.dto.UpdateRoleCommand;
import vn.com.be_crm.application.auth.mapper.RoleCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.auth.repository.IRoleRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/**
 * Use case cập nhật vai trò.
 */
public class UpdateRoleUseCase implements IUseCase<UpdateRoleCommand, RoleResult> {

    private final IRoleRepository repository;

    /** @param repository port lưu trữ Role */
    public UpdateRoleUseCase(IRoleRepository repository) {
        this.repository = repository;
    }

    /**
     * Tìm Role theo ID, cập nhật và trả về result.
     *
     * @param command dữ liệu cập nhật
     * @return RoleResult sau khi cập nhật
     * @throws NotFoundException nếu không tìm thấy Role
     */
    @Override
    public RoleResult execute(UpdateRoleCommand command) {
        var existing = repository.findById(command.getId())
                .orElseThrow(() -> new NotFoundException("Role", command.getId()));
        var updated = RoleCommandMapper.toEntity(command, existing);
        var saved = repository.save(updated);
        return RoleCommandMapper.toResult(saved);
    }
}
