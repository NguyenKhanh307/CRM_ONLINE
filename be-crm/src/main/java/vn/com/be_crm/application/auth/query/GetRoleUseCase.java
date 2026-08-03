package vn.com.be_crm.application.auth.query;

import vn.com.be_crm.application.auth.dto.RoleResult;
import vn.com.be_crm.application.auth.mapper.RoleCommandMapper;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.auth.repository.IRoleRepository;
import vn.com.be_crm.core.error.frontend.NotFoundException;

/**
 * Use case lấy thông tin vai trò theo ID.
 */
public class GetRoleUseCase implements IUseCase<Long, RoleResult> {

    private final IRoleRepository repository;

    /** @param repository port lưu trữ Role */
    public GetRoleUseCase(IRoleRepository repository) {
        this.repository = repository;
    }

    /**
     * Tìm Role theo ID và trả về result.
     *
     * @param id ID vai trò
     * @return RoleResult
     * @throws NotFoundException nếu không tìm thấy
     */
    @Override
    public RoleResult execute(Long id) {
        return repository.findById(id)
                .map(RoleCommandMapper::toResult)
                .orElseThrow(() -> new NotFoundException("Role", id));
    }
}
