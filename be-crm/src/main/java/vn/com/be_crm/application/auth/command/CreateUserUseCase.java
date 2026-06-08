package vn.com.be_crm.application.auth.command;

import vn.com.be_crm.application.auth.dto.CreateUserCommand;
import vn.com.be_crm.application.auth.dto.UserResult;
import vn.com.be_crm.application.auth.mapper.UserCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.auth.entity.User;
import vn.com.be_crm.domain.auth.repository.IUserRepository;

/**
 * Use case tạo mới người dùng.
 */
public class CreateUserUseCase implements IUseCase<CreateUserCommand, UserResult> {

    private final IUserRepository repository;

    /** @param repository port lưu trữ User */
    public CreateUserUseCase(IUserRepository repository) {
        this.repository = repository;
    }

    /**
     * Tạo mới User từ command, lưu và trả về result.
     *
     * @param command dữ liệu tạo mới
     * @return UserResult sau khi lưu
     */
    @Override
    public UserResult execute(CreateUserCommand command) {
        User entity = UserCommandMapper.toEntity(command);
        User saved = repository.save(entity);
        return UserCommandMapper.toResult(saved);
    }
}
