package vn.com.be_crm.application.activity.command;

import vn.com.be_crm.application.activity.dto.ActivityResult;
import vn.com.be_crm.application.activity.dto.UpdateActivityCommand;
import vn.com.be_crm.application.activity.mapper.ActivityCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.activity.entity.Activity;
import vn.com.be_crm.domain.activity.repository.IActivityRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case cập nhật hoạt động. */
public class UpdateActivityUseCase implements IUseCase<UpdateActivityCommand, ActivityResult> {
    private final IActivityRepository repository;
    /** @param repository port lưu trữ Activity */
    public UpdateActivityUseCase(IActivityRepository repository) { this.repository = repository; }
    /**
     * Tìm Activity theo ID, cập nhật và trả về result.
     * @param command dữ liệu cập nhật
     * @return ActivityResult sau khi cập nhật
     * @throws NotFoundException nếu không tìm thấy
     */
    @Override
    public ActivityResult execute(UpdateActivityCommand command) {
        Activity existing = repository.findById(command.getId())
                .orElseThrow(() -> new NotFoundException("Activity", command.getId()));
        return ActivityCommandMapper.toResult(repository.save(ActivityCommandMapper.toEntity(command, existing)));
    }
}
