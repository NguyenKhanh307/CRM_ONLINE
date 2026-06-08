package vn.com.be_crm.application.activity.command;

import vn.com.be_crm.application.activity.dto.ActivityResult;
import vn.com.be_crm.application.activity.dto.CreateActivityCommand;
import vn.com.be_crm.application.activity.mapper.ActivityCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.activity.repository.IActivityRepository;

/** Use case tạo mới hoạt động. */
public class CreateActivityUseCase implements IUseCase<CreateActivityCommand, ActivityResult> {
    private final IActivityRepository repository;
    /** @param repository port lưu trữ Activity */
    public CreateActivityUseCase(IActivityRepository repository) { this.repository = repository; }
    /**
     * Tạo mới Activity từ command và trả về result.
     * @param command dữ liệu tạo mới
     * @return ActivityResult sau khi lưu
     */
    @Override
    public ActivityResult execute(CreateActivityCommand command) {
        return ActivityCommandMapper.toResult(repository.save(ActivityCommandMapper.toEntity(command)));
    }
}
