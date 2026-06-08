package vn.com.be_crm.application.lead.mapper;

import vn.com.be_crm.application.lead.dto.*;
import vn.com.be_crm.domain.lead.entity.LeadAttachment;

/** Chuyển đổi Command ↔ LeadAttachment ↔ LeadAttachmentResult. */
public class LeadAttachmentCommandMapper {

    /**
     * Tạo LeadAttachment từ CreateLeadAttachmentCommand.
     * @param cmd command tạo mới @return domain entity
     */
    public static LeadAttachment toEntity(CreateLeadAttachmentCommand cmd) {
        return LeadAttachment.builder()
                .leadId(cmd.getLeadId()).fileName(cmd.getFileName()).fileUrl(cmd.getFileUrl())
                .fileSize(cmd.getFileSize()).mimeType(cmd.getMimeType()).uploadedBy(cmd.getUploadedBy()).build();
    }

    /**
     * Cập nhật LeadAttachment từ UpdateLeadAttachmentCommand.
     * @param cmd command cập nhật @param e entity hiện tại @return domain entity đã cập nhật
     */
    public static LeadAttachment toEntity(UpdateLeadAttachmentCommand cmd, LeadAttachment e) {
        return LeadAttachment.builder()
                .id(e.getId()).leadId(e.getLeadId())
                .fileName(cmd.getFileName() != null ? cmd.getFileName() : e.getFileName())
                .fileUrl(cmd.getFileUrl() != null ? cmd.getFileUrl() : e.getFileUrl())
                .fileSize(cmd.getFileSize() != null ? cmd.getFileSize() : e.getFileSize())
                .mimeType(cmd.getMimeType() != null ? cmd.getMimeType() : e.getMimeType())
                .uploadedBy(e.getUploadedBy()).createdAt(e.getCreatedAt()).build();
    }

    /**
     * Chuyển LeadAttachment sang LeadAttachmentResult.
     * @param e domain entity @return result DTO
     */
    public static LeadAttachmentResult toResult(LeadAttachment e) {
        return LeadAttachmentResult.builder()
                .id(e.getId()).leadId(e.getLeadId()).fileName(e.getFileName()).fileUrl(e.getFileUrl())
                .fileSize(e.getFileSize()).mimeType(e.getMimeType()).uploadedBy(e.getUploadedBy())
                .createdAt(e.getCreatedAt()).build();
    }

    private LeadAttachmentCommandMapper() {}
}
