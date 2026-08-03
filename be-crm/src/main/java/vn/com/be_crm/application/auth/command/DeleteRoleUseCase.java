package vn.com.be_crm.application.auth.command;

import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.auth.repository.IRoleRepository;
import vn.com.be_crm.core.error.frontend.DomainException;
import vn.com.be_crm.core.error.frontend.NotFoundException;

/**
 * Use case xóa vai trò — không cho phép xóa vai trò hệ thống.
 */
public class DeleteRoleUseCase implements IUseCase<Long, Void> {

    private final IRoleRepository repository;

    /** @param repository port lưu trữ Role */
    public DeleteRoleUseCase(IRoleRepository repository) {
        this.repository = repository;
    }

    /**
     * Kiểm tra tồn tại và không phải vai trò hệ thống, sau đó xóa.
     *
     * @param id ID vai trò cần xóa
     * @return null
     * @throws NotFoundException  nếu không tìm thấy Role
     * @throws DomainException    nếu vai trò là system role
     */
    @Override
    public Void execute(Long id) {
        var role = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Role", id));
        if (Boolean.TRUE.equals(role.getIsSystem())) {
            throw new DomainException("Không thể xóa vai trò hệ thống");
        }
        repository.deleteById(id);
        return null;
    }
}
