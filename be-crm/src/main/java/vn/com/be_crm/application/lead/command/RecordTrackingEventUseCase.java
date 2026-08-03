package vn.com.be_crm.application.lead.command;

import vn.com.be_crm.application.lead.dto.LeadResult;
import vn.com.be_crm.domain.lead.entity.Lead;
import vn.com.be_crm.domain.lead.entity.LeadTrackingEvent;
import vn.com.be_crm.domain.lead.repository.ILeadRepository;
import vn.com.be_crm.domain.lead.repository.ILeadTrackingEventRepository;
import vn.com.be_crm.core.tx.port.ITransactionRunner;

// ghi nhận một hành động web tracking (nút bấm): lưu lịch sử + cộng điểm cho tiềm năng
public class RecordTrackingEventUseCase {
    private final ILeadRepository leadRepo;
    private final ILeadTrackingEventRepository eventRepo;
    private final AddLeadScoreUseCase addScoreUC;
    private final ITransactionRunner tx;

    public RecordTrackingEventUseCase(ILeadRepository leadRepo, ILeadTrackingEventRepository eventRepo,
                                      AddLeadScoreUseCase addScoreUC, ITransactionRunner tx) {
        this.leadRepo = leadRepo; this.eventRepo = eventRepo; this.addScoreUC = addScoreUC; this.tx = tx;
    }

    // trả null nếu không tìm thấy tiềm năng theo mã; sự kiện + điểm + thông báo cùng 1 transaction
    public LeadResult execute(String code, String action, String label, int points) {
        return tx.call(() -> {
            Lead lead = leadRepo.findByCode(code).orElse(null);
            if (lead == null) return null;
            eventRepo.save(LeadTrackingEvent.builder()
                    .leadId(lead.getId()).action(action).label(label).points(points).build());
            return addScoreUC.execute(lead.getId(), points);
        });
    }
}
