package vn.com.be_crm.application.lead.command;

import vn.com.be_crm.application.lead.dto.LeadResult;
import vn.com.be_crm.domain.lead.entity.Lead;
import vn.com.be_crm.domain.lead.entity.LeadTrackingEvent;
import vn.com.be_crm.domain.lead.repository.ILeadRepository;
import vn.com.be_crm.domain.lead.repository.ILeadTrackingEventRepository;

/**
 * Use case ghi nhận một hành động web tracking (nút bấm): lưu lịch sử + cộng điểm cho tiềm năng.
 */
public class RecordTrackingEventUseCase {
    private final ILeadRepository leadRepo;
    private final ILeadTrackingEventRepository eventRepo;
    private final AddLeadScoreUseCase addScoreUC;

    /**
     * @param leadRepo   port lưu trữ Lead
     * @param eventRepo  port lưu trữ LeadTrackingEvent
     * @param addScoreUC use case cộng điểm dùng chung
     */
    public RecordTrackingEventUseCase(ILeadRepository leadRepo, ILeadTrackingEventRepository eventRepo,
                                      AddLeadScoreUseCase addScoreUC) {
        this.leadRepo = leadRepo; this.eventRepo = eventRepo; this.addScoreUC = addScoreUC;
    }

    /**
     * Ghi sự kiện và cộng điểm.
     * @param code   mã tiềm năng (TNW...)
     * @param action mã hành động
     * @param label  nhãn hiển thị
     * @param points số điểm cộng
     * @return LeadResult sau cập nhật, hoặc null nếu không tìm thấy tiềm năng
     */
    public LeadResult execute(String code, String action, String label, int points) {
        Lead lead = leadRepo.findByCode(code).orElse(null);
        if (lead == null) return null;
        eventRepo.save(LeadTrackingEvent.builder()
                .leadId(lead.getId()).action(action).label(label).points(points).build());
        return addScoreUC.execute(lead.getId(), points);
    }
}
