package vn.com.be_crm.application.lead.mapper;

import vn.com.be_crm.application.lead.dto.*;
import vn.com.be_crm.domain.lead.entity.LeadActivity;

/** Chuyển đổi Command ↔ LeadActivity ↔ LeadActivityResult. */
public class LeadActivityCommandMapper {

    /**
     * Tạo LeadActivity từ CreateLeadActivityCommand.
     * @param cmd command tạo mới @return domain entity
     */
    public static LeadActivity toEntity(CreateLeadActivityCommand cmd) {
        return LeadActivity.builder()
                .leadId(cmd.getLeadId()).type(cmd.getType()).subject(cmd.getSubject())
                .content(cmd.getContent()).dueAt(cmd.getDueAt()).completedAt(cmd.getCompletedAt())
                .createdBy(cmd.getCreatedBy()).build();
    }

    /**
     * Cập nhật LeadActivity từ UpdateLeadActivityCommand.
     * @param cmd command cập nhật @param e entity hiện tại @return domain entity đã cập nhật
     */
    public static LeadActivity toEntity(UpdateLeadActivityCommand cmd, LeadActivity e) {
        return LeadActivity.builder()
                .id(e.getId()).leadId(e.getLeadId())
                .type(cmd.getType() != null ? cmd.getType() : e.getType())
                .subject(cmd.getSubject() != null ? cmd.getSubject() : e.getSubject())
                .content(cmd.getContent() != null ? cmd.getContent() : e.getContent())
                .dueAt(cmd.getDueAt() != null ? cmd.getDueAt() : e.getDueAt())
                .completedAt(cmd.getCompletedAt() != null ? cmd.getCompletedAt() : e.getCompletedAt())
                .createdBy(e.getCreatedBy()).createdAt(e.getCreatedAt()).build();
    }

    /**
     * Chuyển LeadActivity sang LeadActivityResult.
     * @param e domain entity @return result DTO
     */
    public static LeadActivityResult toResult(LeadActivity e) {
        return LeadActivityResult.builder()
                .id(e.getId()).leadId(e.getLeadId()).type(e.getType()).subject(e.getSubject())
                .content(e.getContent()).dueAt(e.getDueAt()).completedAt(e.getCompletedAt())
                .createdBy(e.getCreatedBy()).createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt()).build();
    }

    private LeadActivityCommandMapper() {}
}
