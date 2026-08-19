package vn.com.be_crm.application.campaign.command;

import vn.com.be_crm.application.campaign.dto.SendCampaignEmailCommand;
import vn.com.be_crm.core.email.port.IEmailService;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.campaign.entity.Campaign;
import vn.com.be_crm.domain.campaign.entity.CampaignMember;
import vn.com.be_crm.domain.campaign.enums.CampaignMemberStatus;
import vn.com.be_crm.domain.campaign.repository.ICampaignMemberRepository;
import vn.com.be_crm.domain.campaign.repository.ICampaignRepository;
import vn.com.be_crm.core.error.frontend.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Use case gửi email hàng loạt cho thành viên chiến dịch (execution).
 * Gửi tới các thành viên có email hợp lệ và chưa hủy đăng ký; cập nhật trạng thái sent + sentAt.
 */
public class SendCampaignEmailUseCase implements IUseCase<SendCampaignEmailCommand, Integer> {
    private final ICampaignMemberRepository memberRepo;
    private final ICampaignRepository campaignRepo;
    private final IEmailService emailService;
    private final String frontendBaseUrl;

    /** @param memberRepo thành viên chiến dịch @param campaignRepo chiến dịch (lấy mã cho link)
     * @param emailService port gửi email @param frontendBaseUrl gốc URL FE để dựng link landing page */
    public SendCampaignEmailUseCase(ICampaignMemberRepository memberRepo, ICampaignRepository campaignRepo,
                                     IEmailService emailService, String frontendBaseUrl) {
        this.memberRepo = memberRepo;
        this.campaignRepo = campaignRepo;
        this.emailService = emailService;
        this.frontendBaseUrl = frontendBaseUrl;
    }

    /**
     * Gửi email cho toàn bộ thành viên hợp lệ của chiến dịch — mỗi email kèm nút "Xem tại đây" trỏ
     * về landing page (/tracking-demo) gắn đúng mã chiến dịch.
     * @param cmd chứa campaignId, subject, body @return số email đã gửi thành công
     */
    @Override public Integer execute(SendCampaignEmailCommand cmd) {
        Campaign campaign = campaignRepo.findById(cmd.getCampaignId())
                .orElseThrow(() -> new NotFoundException("Campaign", cmd.getCampaignId()));
        String trackingLink = trimTrailingSlash(frontendBaseUrl) + "/tracking-demo?utm_campaign=" + campaign.getCode();

        List<CampaignMember> members = memberRepo.findAllByCampaignId(cmd.getCampaignId());
        int sent = 0;
        for (CampaignMember m : members) {
            // Bỏ qua thành viên không có email hoặc đã hủy đăng ký
            if (m.getEmail() == null || m.getEmail().isBlank()) continue;
            if (m.getStatus() == CampaignMemberStatus.unsubscribed) continue;
            try {
                emailService.sendCampaignEmail(m.getEmail(), m.getName(), cmd.getSubject(), cmd.getBody(), trackingLink);
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

    // bỏ dấu '/' cuối URL để ghép path
    private String trimTrailingSlash(String url) {
        if (url == null) return "";
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }
}
