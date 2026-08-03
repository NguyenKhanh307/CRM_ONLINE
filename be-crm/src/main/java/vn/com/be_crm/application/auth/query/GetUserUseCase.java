package vn.com.be_crm.application.auth.query;

import vn.com.be_crm.application.auth.dto.UserResult;
import vn.com.be_crm.application.auth.mapper.UserCommandMapper;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.auth.repository.IUserRepository;
import vn.com.be_crm.core.error.frontend.NotFoundException;

/**
 * Use case lấy thông tin người dùng theo ID.
 */
public class GetUserUseCase implements IUseCase<Long, UserResult> {

    private final IUserRepository repository;

    /** @param repository port lưu trữ User */
    public GetUserUseCase(IUserRepository repository) {
        this.repository = repository;
    }

    /**
     * Tìm User theo ID và trả về result.
     *
     * @param id ID người dùng
     * @return UserResult
     * @throws NotFoundException nếu không tìm thấy
     */
    @Override
    public UserResult execute(Long id) {
        return repository.findById(id)
                .map(UserCommandMapper::toResult)
                .orElseThrow(() -> new NotFoundException("User", id));
    }
}
