package vn.com.be_crm.application.activity.query;

import vn.com.be_crm.application.activity.dto.ActivityResult;
import vn.com.be_crm.application.activity.mapper.ActivityCommandMapper;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.activity.repository.IActivityRepository;
import vn.com.be_crm.core.error.frontend.NotFoundException;

/** Use case lấy hoạt động theo ID. */
public class GetActivityUseCase implements IUseCase<Long, ActivityResult> {
    private final IActivityRepository repository;
    /** @param repository port lưu trữ Activity */
    public GetActivityUseCase(IActivityRepository repository) { this.repository = repository; }
    /**
     * Tìm Activity theo ID và trả về result.
     * @param id ID hoạt động
     * @return ActivityResult
     * @throws NotFoundException nếu không tìm thấy
     */
    @Override
    public ActivityResult execute(Long id) {
        return repository.findById(id).map(ActivityCommandMapper::toResult)
                .orElseThrow(() -> new NotFoundException("Activity", id));
    }
}
