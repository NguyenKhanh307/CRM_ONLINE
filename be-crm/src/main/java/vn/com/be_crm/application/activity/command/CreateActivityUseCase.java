package vn.com.be_crm.application.activity.command;

import vn.com.be_crm.application.activity.dto.ActivityResult;
import vn.com.be_crm.application.activity.dto.CreateActivityCommand;
import vn.com.be_crm.application.activity.mapper.ActivityCommandMapper;
import vn.com.be_crm.application.lead.command.AddLeadScoreUseCase;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.activity.entity.Activity;
import vn.com.be_crm.domain.activity.enums.ActivityType;
import vn.com.be_crm.domain.activity.repository.IActivityRepository;

import java.util.Map;

/** Use case tạo mới hoạt động. Nếu hoạt động gắn với tiềm năng (targetType=lead) thì cộng điểm cho tiềm năng. */
public class CreateActivityUseCase implements IUseCase<CreateActivityCommand, ActivityResult> {
    /** targetType đại diện cho tiềm năng. */
    private static final String LEAD_TARGET = "lead";
    /** Điểm cộng cho tiềm năng theo loại hoạt động (có thể điều chỉnh). */
    private static final Map<ActivityType, Integer> LEAD_POINTS = Map.of(
            ActivityType.call, 10,
            ActivityType.meeting, 20,
            ActivityType.email, 5,
            ActivityType.task, 5,
            ActivityType.note, 2);

    private final IActivityRepository repository;
    private final AddLeadScoreUseCase addLeadScoreUC;

    /**
     * @param repository     port lưu trữ Activity
     * @param addLeadScoreUC use case cộng điểm tiềm năng dùng chung
     */
    public CreateActivityUseCase(IActivityRepository repository, AddLeadScoreUseCase addLeadScoreUC) {
        this.repository = repository;
        this.addLeadScoreUC = addLeadScoreUC;
    }

    /**
     * Tạo mới Activity từ command, lưu, và cộng điểm cho tiềm năng nếu liên quan.
     * @param command dữ liệu tạo mới
     * @return ActivityResult sau khi lưu
     */
    @Override
    public ActivityResult execute(CreateActivityCommand command) {
        Activity saved = repository.save(ActivityCommandMapper.toEntity(command));
        if (LEAD_TARGET.equals(saved.getTargetType()) && saved.getTargetId() != null && saved.getType() != null) {
            int points = LEAD_POINTS.getOrDefault(saved.getType(), 0);
            if (points > 0) addLeadScoreUC.execute(saved.getTargetId(), points);
        }
        return ActivityCommandMapper.toResult(saved);
    }
}
