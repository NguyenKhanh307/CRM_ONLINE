package vn.com.be_crm.application.lead.command;

import vn.com.be_crm.application.lead.dto.LeadResult;
import vn.com.be_crm.application.lead.mapper.LeadCommandMapper;
import vn.com.be_crm.application.notification.command.CreateNotificationUseCase;
import vn.com.be_crm.domain.auth.repository.IUserRoleRepository;
import vn.com.be_crm.domain.lead.entity.Lead;
import vn.com.be_crm.domain.lead.enums.LeadStatus;
import vn.com.be_crm.domain.lead.repository.ILeadRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Use case dùng chung để cộng điểm cho tiềm năng (từ web tracking hoặc hoạt động).
 * Khi tổng điểm vượt mốc 50 (lần đầu): tự chuyển status sang qualified và gửi thông báo
 * cho người phụ trách (owner nếu có, cùng các user role ADMIN/SALES_MANAGER/SALES_STAFF).
 */
public class AddLeadScoreUseCase {
    /** Mốc điểm để đánh giá tiềm năng đủ điều kiện. */
    public static final int QUALIFY_THRESHOLD = 50;
    /** Các role nhận thông báo tiềm năng nóng (admin, quản lý, và nhân viên kinh doanh). */
    private static final List<String> NOTIFY_ROLE_CODES = List.of("ADMIN", "SALES_MANAGER", "SALES_STAFF");

    private final ILeadRepository leadRepo;
    private final CreateNotificationUseCase createNotificationUC;
    private final IUserRoleRepository userRoleRepo;

    /**
     * @param leadRepo             port lưu trữ Lead
     * @param createNotificationUC use case tạo thông báo
     * @param userRoleRepo         port tra cứu user theo role
     */
    public AddLeadScoreUseCase(ILeadRepository leadRepo, CreateNotificationUseCase createNotificationUC,
                               IUserRoleRepository userRoleRepo) {
        this.leadRepo = leadRepo;
        this.createNotificationUC = createNotificationUC;
        this.userRoleRepo = userRoleRepo;
    }

    /**
     * Cộng điểm cho tiềm năng và xử lý ngưỡng qualified.
     * @param leadId ID tiềm năng @param points số điểm cộng thêm
     * @return LeadResult sau khi cập nhật, hoặc null nếu không tìm thấy tiềm năng
     */
    public LeadResult execute(Long leadId, int points) {
        Lead lead = leadRepo.findById(leadId).orElse(null);
        if (lead == null) return null;

        int oldScore = lead.getScore() != null ? lead.getScore() : 0;
        int newScore = oldScore + points;
        boolean crossing = oldScore <= QUALIFY_THRESHOLD && newScore > QUALIFY_THRESHOLD;

        // Tự động: vượt ngưỡng → qualified; tương tác đầu tiên trên tiềm năng còn 'new' → contacting.
        LeadStatus newStatus;
        if (crossing) newStatus = LeadStatus.qualified;
        else if (lead.getStatus() == LeadStatus.new_) newStatus = LeadStatus.contacting;
        else newStatus = lead.getStatus();
        Lead updated = lead.toBuilder().score(newScore).status(newStatus).build();
        Lead saved = leadRepo.save(updated);

        if (crossing) {
            notifyOwners(saved, newScore);
        }
        return LeadCommandMapper.toResult(saved);
    }

    /** Gửi thông báo "tiềm năng nóng" cho người phụ trách (admin/manager/sale + owner nếu có). */
    private void notifyOwners(Lead lead, int score) {
        List<Long> recipients = new ArrayList<>(userRoleRepo.findUserIdsByRoleCodes(NOTIFY_ROLE_CODES));
        if (lead.getOwnerId() != null) recipients.add(lead.getOwnerId());
        String title = "Tiềm năng nóng: " + lead.getCode();
        String content = "Tiềm năng " + lead.getName() + " (" + lead.getCode() + ") đã đạt "
                + score + " điểm và được đánh giá đủ điều kiện (qualified).";
        createNotificationUC.execute(recipients, "lead_hot", title, content, lead.getId());
    }
}
