package vn.com.be_crm.application.auth.command;

import vn.com.be_crm.application.auth.dto.CreatePermissionCommand;
import vn.com.be_crm.application.auth.dto.PermissionResult;
import vn.com.be_crm.application.auth.mapper.PermissionCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.auth.repository.IPermissionRepository;

/**
 * Use case tạo mới quyền hạn.
 */
public class CreatePermissionUseCase implements IUseCase<CreatePermissionCommand, PermissionResult> {

    private final IPermissionRepository repository;

    /** @param repository port lưu trữ Permission */
    public CreatePermissionUseCase(IPermissionRepository repository) {
        this.repository = repository;
    }

    /**
     * Tạo mới Permission từ command và trả về result.
     *
     * @param command dữ liệu tạo mới
     * @return PermissionResult sau khi lưu
     */
    @Override
    public PermissionResult execute(CreatePermissionCommand command) {
        var entity = PermissionCommandMapper.toEntity(command);
        var saved = repository.save(entity);
        return PermissionCommandMapper.toResult(saved);
    }
}
