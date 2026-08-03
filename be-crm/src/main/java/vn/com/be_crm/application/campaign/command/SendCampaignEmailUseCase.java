package vn.com.be_crm.application.campaign.command;

import vn.com.be_crm.application.campaign.dto.SendCampaignEmailCommand;
import vn.com.be_crm.core.email.port.IEmailService;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.campaign.entity.CampaignMember;
import vn.com.be_crm.domain.campaign.enums.CampaignMemberStatus;
import vn.com.be_crm.domain.campaign.repository.ICampaignMemberRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Use case gửi email hàng loạt cho thành viên chiến dịch (execution).
 * Gửi tới các thành viên có email hợp lệ và chưa hủy đăng ký; cập nhật trạng thái sent + sentAt.
 */
public class SendCampaignEmailUseCase implements IUseCase<SendCampaignEmailCommand, Integer> {
    private final ICampaignMemberRepository memberRepo;
    private final IEmailService emailService;

    /** @param memberRepo thành viên chiến dịch @param emailService port gửi email */
    public SendCampaignEmailUseCase(ICampaignMemberRepository memberRepo, IEmailService emailService) {
        this.memberRepo = memberRepo;
        this.emailService = emailService;
    }

    /**
     * Gửi email cho toàn bộ thành viên hợp lệ của chiến dịch.
     * @param cmd chứa campaignId, subject, body @return số email đã gửi thành công
     */
    @Override public Integer execute(SendCampaignEmailCommand cmd) {
        List<CampaignMember> members = memberRepo.findAllByCampaignId(cmd.getCampaignId());
        int sent = 0;
        for (CampaignMember m : members) {
            // Bỏ qua thành viên không có email hoặc đã hủy đăng ký
            if (m.getEmail() == null || m.getEmail().isBlank()) continue;
            if (m.getStatus() == CampaignMemberStatus.unsubscribed) continue;
            try {
                emailService.sendCampaignEmail(m.getEmail(), m.getName(), cmd.getSubject(), cmd.getBody());
                memberRepo.save(m.toBuilder()
                        .status(CampaignMemberStatus.sent).sentAt(LocalDateTime.now()).build());
                sent++;
            } catch (Exception ex) {
                // Đánh dấu gửi lỗi (bounced) nhưng không dừng cả lô
                memberRepo.save(m.toBuilder().status(CampaignMemberStatus.bounced).build());
            }
        }
        return sent;
    }
}
