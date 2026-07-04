package vn.com.be_crm.application.campaign.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Input DTO khi gửi email hàng loạt cho thành viên chiến dịch. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class SendCampaignEmailCommand {
    /** ID chiến dịch — controller set từ path. */
    private Long campaignId;
    @NotBlank(message = "Tiêu đề email không được để trống") private String subject;
    @NotBlank(message = "Nội dung email không được để trống") private String body;
}
