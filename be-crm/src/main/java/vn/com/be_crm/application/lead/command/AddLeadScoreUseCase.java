package vn.com.be_crm.application.lead.command;

import vn.com.be_crm.application.lead.dto.LeadResult;
import vn.com.be_crm.application.lead.mapper.LeadCommandMapper;
import vn.com.be_crm.application.notification.command.CreateNotificationUseCase;
import vn.com.be_crm.domain.auth.repository.IUserRoleRepository;
import vn.com.be_crm.domain.lead.entity.Lead;
import vn.com.be_crm.domain.lead.enums.LeadStatus;
import vn.com.be_crm.domain.lead.repository.ILeadRepository;
import vn.com.be_crm.core.tx.port.ITransactionRunner;

import java.util.List;

// cộng điểm cho tiềm năng (dùng chung cho web tracking + hoạt động); vượt mốc 50 lần đầu
// KHÔNG còn tự đổi trạng thái — chỉ báo cho người liên quan để họ chủ động tiếp cận, việc chuyển
// đổi tiềm năng vẫn là thao tác tay (xem UpdateLeadUseCase). Chưa có người phụ trách (pool chung)
// -> báo TOÀN BỘ nhân viên (ai cũng có thể nhận); đã có người phụ trách -> CHỈ báo người đó, quản
// lý không cc (quản lý chỉ nhận tin do nhân viên chủ động chuyển qua, xem LeadWorkflowUseCase.claim)
public class AddLeadScoreUseCase {
    public static final int QUALIFY_THRESHOLD = 50;
    private static final List<String> STAFF_ROLES = List.of("SALES_STAFF");

    private final ILeadRepository leadRepo;
    private final CreateNotificationUseCase createNotificationUC;
    private final IUserRoleRepository userRoleRepo;
    private final ITransactionRunner tx;

    public AddLeadScoreUseCase(ILeadRepository leadRepo, CreateNotificationUseCase createNotificationUC,
                               IUserRoleRepository userRoleRepo, ITransactionRunner tx) {
        this.leadRepo = leadRepo;
        this.createNotificationUC = createNotificationUC;
        this.userRoleRepo = userRoleRepo;
        this.tx = tx;
    }

    // trả null nếu không tìm thấy tiềm năng; cộng điểm + thông báo chạy trong 1 transaction
    public LeadResult execute(Long leadId, int points) {
        return tx.call(() -> executeInTx(leadId, points));
    }

    private LeadResult executeInTx(Long leadId, int points) {
        Lead lead = leadRepo.findById(leadId).orElse(null);
        if (lead == null) return null;

        int oldScore = lead.getScore() != null ? lead.getScore() : 0;
        int newScore = oldScore + points;
        boolean crossing = oldScore <= QUALIFY_THRESHOLD && newScore > QUALIFY_THRESHOLD;

        // Tự động: tương tác đầu tiên trên tiềm năng còn 'new' → contacting (không liên quan điểm số).
        // Vượt ngưỡng KHÔNG còn tự đổi trạng thái — chỉ báo cho người phụ trách tiếp cận.
        LeadStatus current = lead.getStatus();
        LeadStatus newStatus = current == LeadStatus.new_ ? LeadStatus.contacting : current;
        Lead updated = lead.toBuilder().score(newScore).status(newStatus).build();
        Lead saved = leadRepo.save(updated);

        if (crossing) {
            notifyOwners(saved, newScore);
        }
        return LeadCommandMapper.toResult(saved);
    }

    private void notifyOwners(Lead lead, int score) {
        // chưa có người phụ trách -> báo toàn bộ nhân viên; đã có -> chỉ báo đúng người đó
        List<Long> recipients = lead.getOwnerId() != null
                ? List.of(lead.getOwnerId())
                : userRoleRepo.findUserIdsByRoleCodes(STAFF_ROLES);
        String title = "Tiềm năng nóng: " + lead.getCode();
        String content = "Tiềm năng " + lead.getName() + " (" + lead.getCode() + ") đã đạt "
                + score + " điểm — hãy chủ động tiếp cận trao đổi.";
        createNotificationUC.execute(recipients, "lead_hot", title, content, "lead", lead.getId());
    }
}
