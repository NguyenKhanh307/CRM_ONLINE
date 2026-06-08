package vn.com.be_crm.application.auth.command;

import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.auth.repository.IUserRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/**
 * Use case xóa mềm người dùng (set deleted_at).
 */
public class DeleteUserUseCase implements IUseCase<Long, Void> {

    private final IUserRepository repository;

    /** @param repository port lưu trữ User */
    public DeleteUserUseCase(IUserRepository repository) {
        this.repository = repository;
    }

    /**
     * Kiểm tra tồn tại rồi xóa mềm User theo ID.
     *
     * @param id ID người dùng cần xóa
     * @return null
     * @throws NotFoundException nếu không tìm thấy User
     */
    @Override
    public Void execute(Long id) {
        repository.findById(id)
                .orElseThrow(() -> new NotFoundException("User", id));
        repository.deleteById(id);
        return null;
    }
}
