package vn.com.be_crm.application.lead.mapper;

import vn.com.be_crm.application.lead.dto.*;
import vn.com.be_crm.domain.lead.entity.Lead;
import vn.com.be_crm.domain.lead.enums.LeadStatus;

// chuyển đổi Command <-> Lead <-> LeadResult
public class LeadCommandMapper {

    public static Lead toEntity(CreateLeadCommand cmd) {
        return Lead.builder()
                .code(cmd.getCode()).name(cmd.getName())
                .companyName(cmd.getCompanyName()).leadType(cmd.getLeadType())
                .ownerId(cmd.getOwnerId()).contactId(cmd.getContactId())
                .taxCode(cmd.getTaxCode()).website(cmd.getWebsite()).industry(cmd.getIndustry())
                .source(cmd.getSource()).campaignId(cmd.getCampaignId())
                .status(cmd.getStatus() != null ? cmd.getStatus() : LeadStatus.new_)
                .score(0).phone(cmd.getPhone())
                .email(cmd.getEmail())
                .note(cmd.getNote()).build();
    }

    public static Lead toEntity(UpdateLeadCommand cmd, Lead e) {
        return Lead.builder()
                .id(e.getId()).code(e.getCode())
                .name(cmd.getName() != null ? cmd.getName() : e.getName())
                .companyName(cmd.getCompanyName() != null ? cmd.getCompanyName() : e.getCompanyName())
                .leadType(cmd.getLeadType() != null ? cmd.getLeadType() : e.getLeadType())
                .ownerId(cmd.getOwnerId() != null ? cmd.getOwnerId() : e.getOwnerId())
                .contactId(cmd.getContactId() != null ? cmd.getContactId() : e.getContactId())
                .convertedOpportunityId(cmd.getConvertedOpportunityId() != null ? cmd.getConvertedOpportunityId() : e.getConvertedOpportunityId())
                .taxCode(cmd.getTaxCode() != null ? cmd.getTaxCode() : e.getTaxCode())
                .website(cmd.getWebsite() != null ? cmd.getWebsite() : e.getWebsite())
                .industry(cmd.getIndustry() != null ? cmd.getIndustry() : e.getIndustry())
                .source(cmd.getSource() != null ? cmd.getSource() : e.getSource())
                .campaignId(cmd.getCampaignId() != null ? cmd.getCampaignId() : e.getCampaignId())
                // status: đổi tự động (chấm điểm) hoặc tự tay qua Update (không còn action convert riêng)
                .status(cmd.getStatus() != null ? cmd.getStatus() : e.getStatus())
                .score(e.getScore())
                .phone(cmd.getPhone() != null ? cmd.getPhone() : e.getPhone())
                .email(cmd.getEmail() != null ? cmd.getEmail() : e.getEmail())
                .note(cmd.getNote() != null ? cmd.getNote() : e.getNote())
                .createdBy(e.getCreatedBy()).updatedBy(e.getUpdatedBy())
                .createdAt(e.getCreatedAt()).build();
    }

    public static LeadResult toResult(Lead e) {
        return LeadResult.builder()
                .id(e.getId()).code(e.getCode()).name(e.getName())
                .companyName(e.getCompanyName()).leadType(e.getLeadType())
                .ownerId(e.getOwnerId())
                .contactId(e.getContactId())
                .convertedOpportunityId(e.getConvertedOpportunityId())
                .taxCode(e.getTaxCode()).website(e.getWebsite()).industry(e.getIndustry())
                .source(e.getSource()).campaignId(e.getCampaignId())
                .status(e.getStatus()).score(e.getScore())
                .phone(e.getPhone())
                .email(e.getEmail())
                .note(e.getNote()).createdBy(e.getCreatedBy()).updatedBy(e.getUpdatedBy())
                .createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt()).build();
    }

    private LeadCommandMapper() {}
}
