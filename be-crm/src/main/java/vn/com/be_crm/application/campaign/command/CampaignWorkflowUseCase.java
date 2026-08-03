package vn.com.be_crm.application.campaign.command;

import vn.com.be_crm.application.campaign.dto.CampaignResult;
import vn.com.be_crm.application.campaign.mapper.CampaignCommandMapper;
import vn.com.be_crm.domain.campaign.entity.Campaign;
import vn.com.be_crm.domain.campaign.enums.CampaignStatus;
import vn.com.be_crm.domain.campaign.repository.ICampaignRepository;
import vn.com.be_crm.core.error.frontend.NotFoundException;

/**
 * Use case điều phối trạng thái chiến dịch (theo hành động, không sửa tay):
 * schedule (→ scheduled) / start (→ running) / pause (→ paused) / complete (→ completed) / cancel (→ cancelled).
 */
public class CampaignWorkflowUseCase {
    private final ICampaignRepository repo;

    /** @param repo port lưu trữ chiến dịch */
    public CampaignWorkflowUseCase(ICampaignRepository repo) { this.repo = repo; }

    /** Lên lịch chiến dịch (→ scheduled). @param id ID @return chiến dịch sau cập nhật */
    public CampaignResult schedule(Long id) { return transition(id, CampaignStatus.scheduled); }
    /** Bắt đầu chạy chiến dịch (→ running). @param id ID @return chiến dịch sau cập nhật */
    public CampaignResult start(Long id) { return transition(id, CampaignStatus.running); }
    /** Tạm dừng chiến dịch (→ paused). @param id ID @return chiến dịch sau cập nhật */
    public CampaignResult pause(Long id) { return transition(id, CampaignStatus.paused); }
    /** Hoàn tất chiến dịch (→ completed). @param id ID @return chiến dịch sau cập nhật */
    public CampaignResult complete(Long id) { return transition(id, CampaignStatus.completed); }
    /** Hủy chiến dịch (→ cancelled). @param id ID @return chiến dịch sau cập nhật */
    public CampaignResult cancel(Long id) { return transition(id, CampaignStatus.cancelled); }

    /** Chuyển trạng thái có guard. @param id ID @param target trạng thái đích @return kết quả */
    private CampaignResult transition(Long id, CampaignStatus target) {
        Campaign c = repo.findById(id).orElseThrow(() -> new NotFoundException("Campaign not found: " + id));
        c.getStatus().ensureCanTransitionTo(target);
        return CampaignCommandMapper.toResult(repo.save(c.toBuilder().status(target).build()));
    }
}
