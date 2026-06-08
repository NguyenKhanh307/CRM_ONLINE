package vn.com.be_crm.application.activity.command;

import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.activity.repository.IActivityRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case xóa hoạt động. */
public class DeleteActivityUseCase implements IUseCase<Long, Void> {
    private final IActivityRepository repository;
    /** @param repository port lưu trữ Activity */
    public DeleteActivityUseCase(IActivityRepository repository) { this.repository = repository; }
    /**
     * Kiểm tra tồn tại rồi xóa Activity theo ID.
     * @param id ID hoạt động cần xóa
     * @return null
     * @throws NotFoundException nếu không tìm thấy
     */
    @Override
    public Void execute(Long id) {
        repository.findById(id).orElseThrow(() -> new NotFoundException("Activity", id));
        repository.deleteById(id);
        return null;
    }
}
