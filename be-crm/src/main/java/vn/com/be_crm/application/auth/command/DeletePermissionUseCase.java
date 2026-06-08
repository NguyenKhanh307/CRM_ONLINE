package vn.com.be_crm.application.auth.command;

import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.auth.repository.IPermissionRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/**
 * Use case xóa quyền hạn.
 */
public class DeletePermissionUseCase implements IUseCase<Long, Void> {

    private final IPermissionRepository repository;

    /** @param repository port lưu trữ Permission */
    public DeletePermissionUseCase(IPermissionRepository repository) {
        this.repository = repository;
    }

    /**
     * Kiểm tra tồn tại rồi xóa Permission theo ID.
     *
     * @param id ID quyền cần xóa
     * @return null
     * @throws NotFoundException nếu không tìm thấy Permission
     */
    @Override
    public Void execute(Long id) {
        repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Permission", id));
        repository.deleteById(id);
        return null;
    }
}
