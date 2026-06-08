package vn.com.be_crm.application.auth.command;

import vn.com.be_crm.application.auth.dto.UpdateUserCommand;
import vn.com.be_crm.application.auth.dto.UserResult;
import vn.com.be_crm.application.auth.mapper.UserCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.auth.entity.User;
import vn.com.be_crm.domain.auth.repository.IUserRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/**
 * Use case cập nhật thông tin người dùng.
 */
public class UpdateUserUseCase implements IUseCase<UpdateUserCommand, UserResult> {

    private final IUserRepository repository;

    /** @param repository port lưu trữ User */
    public UpdateUserUseCase(IUserRepository repository) {
        this.repository = repository;
    }

    /**
     * Tìm User theo ID, cập nhật và trả về result.
     *
     * @param command dữ liệu cập nhật (bao gồm id)
     * @return UserResult sau khi cập nhật
     * @throws NotFoundException nếu không tìm thấy User
     */
    @Override
    public UserResult execute(UpdateUserCommand command) {
        User existing = repository.findById(command.getId())
                .orElseThrow(() -> new NotFoundException("User", command.getId()));
        User updated = UserCommandMapper.toEntity(command, existing);
        User saved = repository.save(updated);
        return UserCommandMapper.toResult(saved);
    }
}
