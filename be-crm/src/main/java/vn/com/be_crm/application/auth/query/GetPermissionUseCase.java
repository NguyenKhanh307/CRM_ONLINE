package vn.com.be_crm.application.auth.query;

import vn.com.be_crm.application.auth.dto.PermissionResult;
import vn.com.be_crm.application.auth.mapper.PermissionCommandMapper;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.auth.repository.IPermissionRepository;
import vn.com.be_crm.core.error.frontend.NotFoundException;

/**
 * Use case lấy thông tin quyền hạn theo ID.
 */
public class GetPermissionUseCase implements IUseCase<Long, PermissionResult> {

    private final IPermissionRepository repository;

    /** @param repository port lưu trữ Permission */
    public GetPermissionUseCase(IPermissionRepository repository) {
        this.repository = repository;
    }

    /**
     * Tìm Permission theo ID và trả về result.
     *
     * @param id ID quyền
     * @return PermissionResult
     * @throws NotFoundException nếu không tìm thấy
     */
    @Override
    public PermissionResult execute(Long id) {
        return repository.findById(id)
                .map(PermissionCommandMapper::toResult)
                .orElseThrow(() -> new NotFoundException("Permission", id));
    }
}
