package vn.com.be_crm.application.lead.command;

import vn.com.be_crm.application.lead.dto.LeadResult;
import vn.com.be_crm.domain.lead.entity.Lead;
import vn.com.be_crm.domain.lead.entity.LeadTrackingEvent;
import vn.com.be_crm.domain.lead.repository.ILeadRepository;
import vn.com.be_crm.domain.lead.repository.ILeadTrackingEventRepository;
import vn.com.be_crm.core.tx.port.ITransactionRunner;

// xử lý nộp form web tracking: cập nhật thông tin liên hệ vào tiềm năng, ghi lịch sử + cộng điểm
public class SubmitTrackingFormUseCase {
    private final ILeadRepository leadRepo;
    private final ILeadTrackingEventRepository eventRepo;
    private final AddLeadScoreUseCase addScoreUC;
    private final ITransactionRunner tx;

    public SubmitTrackingFormUseCase(ILeadRepository leadRepo, ILeadTrackingEventRepository eventRepo,
                                     AddLeadScoreUseCase addScoreUC, ITransactionRunner tx) {
        this.leadRepo = leadRepo; this.eventRepo = eventRepo; this.addScoreUC = addScoreUC; this.tx = tx;
    }

    // name/companyName/email/phone/note: chỉ ghi đè nếu form gửi lên (không rỗng); trả null
    // nếu không tìm thấy tiềm năng theo mã; cập nhật + sự kiện + điểm cùng 1 transaction
    public LeadResult execute(String code, String name, String companyName, String email,
                              String phone, String note, int points) {
        return tx.call(() -> {
            Lead lead = leadRepo.findByCode(code).orElse(null);
            if (lead == null) return null;

            Lead updated = lead.toBuilder()
                    .name(isBlank(name) ? lead.getName() : name)
                    .companyName(isBlank(companyName) ? lead.getCompanyName() : companyName)
                    .email(isBlank(email) ? lead.getEmail() : email)
                    .phone(isBlank(phone) ? lead.getPhone() : phone)
                    .note(isBlank(note) ? lead.getNote() : note)
                    .build();
            leadRepo.save(updated);

            eventRepo.save(LeadTrackingEvent.builder()
                    .leadId(lead.getId()).action("submit_form").label("Nộp form liên hệ").points(points).build());
            return addScoreUC.execute(lead.getId(), points);
        });
    }

    private boolean isBlank(String s) { return s == null || s.isBlank(); }
}
