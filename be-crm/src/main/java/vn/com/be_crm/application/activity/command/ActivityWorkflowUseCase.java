package vn.com.be_crm.application.activity.command;

import vn.com.be_crm.application.activity.dto.ActivityResult;
import vn.com.be_crm.application.activity.mapper.ActivityCommandMapper;
import vn.com.be_crm.domain.activity.entity.Activity;
import vn.com.be_crm.domain.activity.enums.ActivityStatus;
import vn.com.be_crm.domain.activity.repository.IActivityRepository;
import vn.com.be_crm.core.error.frontend.NotFoundException;

/**
 * Use case điều phối trạng thái hoạt động (theo hành động, không sửa tay):
 * start (planned → in_progress) / complete (→ done) / cancel (→ cancelled).
 */
public class ActivityWorkflowUseCase {
    private final IActivityRepository repo;

    /** @param repo port lưu trữ Activity */
    public ActivityWorkflowUseCase(IActivityRepository repo) { this.repo = repo; }

    /** Bắt đầu thực hiện (planned → in_progress). @param id ID @return hoạt động sau cập nhật */
    public ActivityResult start(Long id) {
        Activity a = load(id);
        a.getStatus().ensureCanTransitionTo(ActivityStatus.in_progress);
        return ActivityCommandMapper.toResult(repo.save(a.toBuilder().status(ActivityStatus.in_progress).build()));
    }

    /** Hoàn thành (→ done). @param id ID @return hoạt động sau cập nhật */
    public ActivityResult complete(Long id) {
        Activity a = load(id);
        a.getStatus().ensureCanTransitionTo(ActivityStatus.done);
        return ActivityCommandMapper.toResult(repo.save(a.toBuilder().status(ActivityStatus.done).build()));
    }

    /** Hủy hoạt động (→ cancelled). @param id ID @return hoạt động sau cập nhật */
    public ActivityResult cancel(Long id) {
        Activity a = load(id);
        a.getStatus().ensureCanTransitionTo(ActivityStatus.cancelled);
        return ActivityCommandMapper.toResult(repo.save(a.toBuilder().status(ActivityStatus.cancelled).build()));
    }

    /**
     * Mở lại khi lỡ bấm nhầm chuyển trạng thái: done → in_progress, cancelled → planned.
     * @param id ID @return hoạt động sau cập nhật
     */
    public ActivityResult reopen(Long id) {
        Activity a = load(id);
        ActivityStatus target = a.getStatus() == ActivityStatus.cancelled ? ActivityStatus.planned : ActivityStatus.in_progress;
        a.getStatus().ensureCanTransitionTo(target);
        return ActivityCommandMapper.toResult(repo.save(a.toBuilder().status(target).build()));
    }

    /** Tải hoạt động theo ID hoặc ném NotFoundException. */
    private Activity load(Long id) {
        return repo.findById(id).orElseThrow(() -> new NotFoundException("Activity not found: " + id));
    }
}
