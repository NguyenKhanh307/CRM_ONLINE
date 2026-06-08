package vn.com.be_crm.application.auth.command;

import vn.com.be_crm.application.auth.dto.PermissionResult;
import vn.com.be_crm.application.auth.dto.UpdatePermissionCommand;
import vn.com.be_crm.application.auth.mapper.PermissionCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.auth.repository.IPermissionRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/**
 * Use case cập nhật quyền hạn.
 */
public class UpdatePermissionUseCase implements IUseCase<UpdatePermissionCommand, PermissionResult> {

    private final IPermissionRepository repository;

    /** @param repository port lưu trữ Permission */
    public UpdatePermissionUseCase(IPermissionRepository repository) {
        this.repository = repository;
    }

    /**
     * Tìm Permission theo ID, cập nhật và trả về result.
     *
     * @param command dữ liệu cập nhật
     * @return PermissionResult sau khi cập nhật
     * @throws NotFoundException nếu không tìm thấy Permission
     */
    @Override
    public PermissionResult execute(UpdatePermissionCommand command) {
        var existing = repository.findById(command.getId())
                .orElseThrow(() -> new NotFoundException("Permission", command.getId()));
        var updated = PermissionCommandMapper.toEntity(command, existing);
        var saved = repository.save(updated);
        return PermissionCommandMapper.toResult(saved);
    }
}
