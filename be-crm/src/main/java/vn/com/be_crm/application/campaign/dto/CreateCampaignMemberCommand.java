package vn.com.be_crm.application.campaign.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Input DTO khi tạo mới thành viên chiến dịch. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateCampaignMemberCommand {
    /** ID chiến dịch — controller set từ path. */
    private Long campaignId;
    private Long leadId;
    private Long contactId;
    @Size(max = 100) private String name;
    @NotBlank(message = "Email không được để trống") @Email(message = "Email không đúng định dạng") @Size(max = 100) private String email;
    @Size(max = 20) private String phone;
}
