package vn.com.be_crm.application.campaign.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.campaign.enums.CampaignMemberStatus;

/** Input DTO khi cập nhật thành viên chiến dịch. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class UpdateCampaignMemberCommand {
    private Long id;
    private Long leadId;
    private Long contactId;
    @Size(max = 100) private String name;
    @Size(max = 100) private String email;
    @Size(max = 20) private String phone;
    private CampaignMemberStatus status;
}
