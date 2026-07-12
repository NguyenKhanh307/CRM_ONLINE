package vn.com.be_crm.application.lead.mapper;

import vn.com.be_crm.application.lead.dto.*;
import vn.com.be_crm.domain.lead.entity.Lead;
import vn.com.be_crm.domain.lead.enums.LeadStatus;

/** Chuyển đổi Command ↔ Lead ↔ LeadResult. */
public class LeadCommandMapper {

    /**
     * Tạo Lead từ CreateLeadCommand.
     * @param cmd command tạo mới @return domain entity
     */
    public static Lead toEntity(CreateLeadCommand cmd) {
        return Lead.builder()
                .code(cmd.getCode()).name(cmd.getName())
                .companyName(cmd.getCompanyName()).leadType(cmd.getLeadType())
                .ownerId(cmd.getOwnerId())
                .customerId(cmd.getCustomerId()).contactId(cmd.getContactId())
                .title(cmd.getTitle()).department(cmd.getDepartment())
                .taxCode(cmd.getTaxCode()).website(cmd.getWebsite()).industry(cmd.getIndustry())
                .source(cmd.getSource()).campaignId(cmd.getCampaignId())
                .status(cmd.getStatus() != null ? cmd.getStatus() : LeadStatus.new_)
                .estimatedValue(cmd.getEstimatedValue()).score(0).phone(cmd.getPhone())
                .email(cmd.getEmail())
                .doNotCall(cmd.getDoNotCall() != null && cmd.getDoNotCall())
                .doNotEmail(cmd.getDoNotEmail() != null && cmd.getDoNotEmail())
                .note(cmd.getNote()).build();
    }

    /**
     * Cập nhật Lead từ UpdateLeadCommand.
     * @param cmd command cập nhật @param e entity hiện tại @return domain entity đã cập nhật
     */
    public static Lead toEntity(UpdateLeadCommand cmd, Lead e) {
        return Lead.builder()
                .id(e.getId()).code(e.getCode())
                .name(cmd.getName() != null ? cmd.getName() : e.getName())
                .companyName(cmd.getCompanyName() != null ? cmd.getCompanyName() : e.getCompanyName())
                .leadType(cmd.getLeadType() != null ? cmd.getLeadType() : e.getLeadType())
                .ownerId(cmd.getOwnerId() != null ? cmd.getOwnerId() : e.getOwnerId())
                .customerId(cmd.getCustomerId() != null ? cmd.getCustomerId() : e.getCustomerId())
                .contactId(cmd.getContactId() != null ? cmd.getContactId() : e.getContactId())
                .convertedOpportunityId(e.getConvertedOpportunityId())
                .title(cmd.getTitle() != null ? cmd.getTitle() : e.getTitle())
                .department(cmd.getDepartment() != null ? cmd.getDepartment() : e.getDepartment())
                .taxCode(cmd.getTaxCode() != null ? cmd.getTaxCode() : e.getTaxCode())
                .website(cmd.getWebsite() != null ? cmd.getWebsite() : e.getWebsite())
                .industry(cmd.getIndustry() != null ? cmd.getIndustry() : e.getIndustry())
                .source(cmd.getSource() != null ? cmd.getSource() : e.getSource())
                .campaignId(cmd.getCampaignId() != null ? cmd.getCampaignId() : e.getCampaignId())
                // status: đổi tự động (chấm điểm) hoặc qua hành động (convert/lose) — không nhận từ command.
                .status(e.getStatus())
                .estimatedValue(cmd.getEstimatedValue() != null ? cmd.getEstimatedValue() : e.getEstimatedValue())
                .score(e.getScore())
                .phone(cmd.getPhone() != null ? cmd.getPhone() : e.getPhone())
                .email(cmd.getEmail() != null ? cmd.getEmail() : e.getEmail())
                .doNotCall(cmd.getDoNotCall() != null ? cmd.getDoNotCall() : e.isDoNotCall())
                .doNotEmail(cmd.getDoNotEmail() != null ? cmd.getDoNotEmail() : e.isDoNotEmail())
                .note(cmd.getNote() != null ? cmd.getNote() : e.getNote())
                .createdBy(e.getCreatedBy()).updatedBy(e.getUpdatedBy())
                .createdAt(e.getCreatedAt()).build();
    }

    /**
     * Chuyển Lead sang LeadResult.
     * @param e domain entity @return result DTO
     */
    public static LeadResult toResult(Lead e) {
        return LeadResult.builder()
                .id(e.getId()).code(e.getCode()).name(e.getName())
                .companyName(e.getCompanyName()).leadType(e.getLeadType())
                .ownerId(e.getOwnerId())
                .customerId(e.getCustomerId()).contactId(e.getContactId())
                .convertedOpportunityId(e.getConvertedOpportunityId())
                .title(e.getTitle()).department(e.getDepartment())
                .taxCode(e.getTaxCode()).website(e.getWebsite()).industry(e.getIndustry())
                .source(e.getSource()).campaignId(e.getCampaignId())
                .status(e.getStatus()).estimatedValue(e.getEstimatedValue()).score(e.getScore())
                .phone(e.getPhone())
                .email(e.getEmail())
                .doNotCall(e.isDoNotCall()).doNotEmail(e.isDoNotEmail())
                .note(e.getNote()).createdBy(e.getCreatedBy()).updatedBy(e.getUpdatedBy())
                .createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt()).build();
    }

    private LeadCommandMapper() {}
}
