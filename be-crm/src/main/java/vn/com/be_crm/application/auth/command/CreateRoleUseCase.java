package vn.com.be_crm.application.auth.command;

import vn.com.be_crm.application.auth.dto.CreateRoleCommand;
import vn.com.be_crm.application.auth.dto.RoleResult;
import vn.com.be_crm.application.auth.mapper.RoleCommandMapper;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.auth.repository.IRoleRepository;

/**
 * Use case tạo mới vai trò.
 */
public class CreateRoleUseCase implements IUseCase<CreateRoleCommand, RoleResult> {

    private final IRoleRepository repository;

    /** @param repository port lưu trữ Role */
    public CreateRoleUseCase(IRoleRepository repository) {
        this.repository = repository;
    }

    /**
     * Tạo mới Role từ command và trả về result.
     *
     * @param command dữ liệu tạo mới
     * @return RoleResult sau khi lưu
     */
    @Override
    public RoleResult execute(CreateRoleCommand command) {
        var entity = RoleCommandMapper.toEntity(command);
        var saved = repository.save(entity);
        return RoleCommandMapper.toResult(saved);
    }
}
