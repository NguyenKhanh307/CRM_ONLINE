package vn.com.be_crm.application.lead.command;

import vn.com.be_crm.application.lead.dto.LeadResult;
import vn.com.be_crm.application.lead.mapper.LeadCommandMapper;
import vn.com.be_crm.application.notification.command.CreateNotificationUseCase;
import vn.com.be_crm.core.notify.port.IManagerResolver;
import vn.com.be_crm.domain.lead.entity.Lead;
import vn.com.be_crm.domain.lead.enums.LeadStatus;
import vn.com.be_crm.domain.lead.repository.ILeadRepository;
import vn.com.be_crm.core.tx.port.ITransactionRunner;

import java.util.ArrayList;
import java.util.List;

// cộng điểm cho tiềm năng (dùng chung cho web tracking + hoạt động); vượt mốc 50 lần đầu
// KHÔNG còn tự đổi trạng thái — chỉ báo cho người phụ trách + quản lý trực tiếp (không broadcast
// toàn bộ) để họ chủ động tiếp cận, việc chuyển đổi tiềm năng vẫn là thao tác tay (xem UpdateLeadUseCase)
public class AddLeadScoreUseCase {
    public static final int QUALIFY_THRESHOLD = 50;

    private final ILeadRepository leadRepo;
    private final CreateNotificationUseCase createNotificationUC;
    private final IManagerResolver managerResolver;
    private final ITransactionRunner tx;

    public AddLeadScoreUseCase(ILeadRepository leadRepo, CreateNotificationUseCase createNotificationUC,
                               IManagerResolver managerResolver, ITransactionRunner tx) {
        this.leadRepo = leadRepo;
        this.createNotificationUC = createNotificationUC;
        this.managerResolver = managerResolver;
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
        List<Long> recipients = new ArrayList<>();
        if (lead.getOwnerId() != null) {
            recipients.add(lead.getOwnerId());
            recipients.addAll(managerResolver.managersOf(lead.getOwnerId()));
        } else {
            recipients.addAll(managerResolver.managersOf(null));
        }
        String title = "Tiềm năng nóng: " + lead.getCode();
        String content = "Tiềm năng " + lead.getName() + " (" + lead.getCode() + ") đã đạt "
                + score + " điểm — hãy chủ động tiếp cận trao đổi.";
        createNotificationUC.execute(recipients, "lead_hot", title, content, "lead", lead.getId());
    }
}
