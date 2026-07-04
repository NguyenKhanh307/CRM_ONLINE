package vn.com.be_crm.application.campaign.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.campaign.enums.CampaignMemberStatus;

import java.time.LocalDateTime;

/** Output DTO cho CampaignMember. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class CampaignMemberResult {
    private Long id;
    private Long campaignId;
    private Long leadId;
    private Long contactId;
    private String name;
    private String email;
    private String phone;
    private CampaignMemberStatus status;
    private LocalDateTime sentAt;
    private LocalDateTime respondedAt;
    private LocalDateTime createdAt;
}
