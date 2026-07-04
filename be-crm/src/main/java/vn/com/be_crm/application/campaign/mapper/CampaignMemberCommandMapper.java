package vn.com.be_crm.application.campaign.mapper;

import vn.com.be_crm.application.campaign.dto.*;
import vn.com.be_crm.domain.campaign.entity.CampaignMember;
import vn.com.be_crm.domain.campaign.enums.CampaignMemberStatus;

/** Chuyển đổi Command ↔ CampaignMember ↔ CampaignMemberResult. */
public class CampaignMemberCommandMapper {

    /**
     * Tạo CampaignMember từ CreateCampaignMemberCommand. Trạng thái mặc định pending.
     * @param cmd command tạo mới @return domain entity
     */
    public static CampaignMember toEntity(CreateCampaignMemberCommand cmd) {
        return CampaignMember.builder()
                .campaignId(cmd.getCampaignId()).leadId(cmd.getLeadId()).contactId(cmd.getContactId())
                .name(cmd.getName()).email(cmd.getEmail()).phone(cmd.getPhone())
                .status(CampaignMemberStatus.pending)
                .build();
    }

    /**
     * Cập nhật CampaignMember từ UpdateCampaignMemberCommand.
     * @param cmd command cập nhật @param e entity hiện tại @return domain entity đã cập nhật
     */
    public static CampaignMember toEntity(UpdateCampaignMemberCommand cmd, CampaignMember e) {
        return CampaignMember.builder()
                .id(e.getId()).campaignId(e.getCampaignId())
                .leadId(cmd.getLeadId() != null ? cmd.getLeadId() : e.getLeadId())
                .contactId(cmd.getContactId() != null ? cmd.getContactId() : e.getContactId())
                .name(cmd.getName() != null ? cmd.getName() : e.getName())
                .email(cmd.getEmail() != null ? cmd.getEmail() : e.getEmail())
                .phone(cmd.getPhone() != null ? cmd.getPhone() : e.getPhone())
                .status(cmd.getStatus() != null ? cmd.getStatus() : e.getStatus())
                .sentAt(e.getSentAt()).respondedAt(e.getRespondedAt())
                .createdAt(e.getCreatedAt()).build();
    }

    /**
     * Chuyển CampaignMember sang CampaignMemberResult.
     * @param e domain entity @return result DTO
     */
    public static CampaignMemberResult toResult(CampaignMember e) {
        return CampaignMemberResult.builder()
                .id(e.getId()).campaignId(e.getCampaignId()).leadId(e.getLeadId()).contactId(e.getContactId())
                .name(e.getName()).email(e.getEmail()).phone(e.getPhone()).status(e.getStatus())
                .sentAt(e.getSentAt()).respondedAt(e.getRespondedAt()).createdAt(e.getCreatedAt()).build();
    }

    private CampaignMemberCommandMapper() {}
}
