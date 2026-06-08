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
                .code(cmd.getCode()).name(cmd.getName()).ownerId(cmd.getOwnerId())
                .customerId(cmd.getCustomerId()).contactId(cmd.getContactId()).source(cmd.getSource())
                .status(cmd.getStatus() != null ? cmd.getStatus() : LeadStatus.new_)
                .estimatedValue(cmd.getEstimatedValue()).phone(cmd.getPhone())
                .email(cmd.getEmail()).note(cmd.getNote()).build();
    }

    /**
     * Cập nhật Lead từ UpdateLeadCommand.
     * @param cmd command cập nhật @param e entity hiện tại @return domain entity đã cập nhật
     */
    public static Lead toEntity(UpdateLeadCommand cmd, Lead e) {
        return Lead.builder()
                .id(e.getId()).code(e.getCode())
                .name(cmd.getName() != null ? cmd.getName() : e.getName())
                .ownerId(cmd.getOwnerId() != null ? cmd.getOwnerId() : e.getOwnerId())
                .customerId(cmd.getCustomerId() != null ? cmd.getCustomerId() : e.getCustomerId())
                .contactId(cmd.getContactId() != null ? cmd.getContactId() : e.getContactId())
                .source(cmd.getSource() != null ? cmd.getSource() : e.getSource())
                .status(cmd.getStatus() != null ? cmd.getStatus() : e.getStatus())
                .estimatedValue(cmd.getEstimatedValue() != null ? cmd.getEstimatedValue() : e.getEstimatedValue())
                .phone(cmd.getPhone() != null ? cmd.getPhone() : e.getPhone())
                .email(cmd.getEmail() != null ? cmd.getEmail() : e.getEmail())
                .note(cmd.getNote() != null ? cmd.getNote() : e.getNote())
                .createdAt(e.getCreatedAt()).build();
    }

    /**
     * Chuyển Lead sang LeadResult.
     * @param e domain entity @return result DTO
     */
    public static LeadResult toResult(Lead e) {
        return LeadResult.builder()
                .id(e.getId()).code(e.getCode()).name(e.getName()).ownerId(e.getOwnerId())
                .customerId(e.getCustomerId()).contactId(e.getContactId()).source(e.getSource())
                .status(e.getStatus()).estimatedValue(e.getEstimatedValue()).phone(e.getPhone())
                .email(e.getEmail()).note(e.getNote()).createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt()).build();
    }

    private LeadCommandMapper() {}
}
