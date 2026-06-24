package vn.com.be_crm.application.lead.command;

import vn.com.be_crm.application.lead.dto.LeadResult;
import vn.com.be_crm.domain.lead.entity.Lead;
import vn.com.be_crm.domain.lead.entity.LeadTrackingEvent;
import vn.com.be_crm.domain.lead.repository.ILeadRepository;
import vn.com.be_crm.domain.lead.repository.ILeadTrackingEventRepository;

/**
 * Use case xử lý nộp form web tracking: cập nhật thông tin liên hệ vào tiềm năng,
 * ghi lịch sử và cộng điểm.
 */
public class SubmitTrackingFormUseCase {
    private final ILeadRepository leadRepo;
    private final ILeadTrackingEventRepository eventRepo;
    private final AddLeadScoreUseCase addScoreUC;

    /**
     * @param leadRepo   port lưu trữ Lead
     * @param eventRepo  port lưu trữ LeadTrackingEvent
     * @param addScoreUC use case cộng điểm dùng chung
     */
    public SubmitTrackingFormUseCase(ILeadRepository leadRepo, ILeadTrackingEventRepository eventRepo,
                                     AddLeadScoreUseCase addScoreUC) {
        this.leadRepo = leadRepo; this.eventRepo = eventRepo; this.addScoreUC = addScoreUC;
    }

    /**
     * Cập nhật thông tin từ form + ghi sự kiện + cộng điểm.
     * @param code        mã tiềm năng (TNW...)
     * @param name        họ tên (nếu có)
     * @param companyName tên công ty (nếu có)
     * @param email       email (nếu có)
     * @param phone       số điện thoại (nếu có)
     * @param note        ghi chú (nếu có)
     * @param points      số điểm cộng
     * @return LeadResult sau cập nhật, hoặc null nếu không tìm thấy tiềm năng
     */
    public LeadResult execute(String code, String name, String companyName, String email,
                              String phone, String note, int points) {
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
    }

    private boolean isBlank(String s) { return s == null || s.isBlank(); }
}
