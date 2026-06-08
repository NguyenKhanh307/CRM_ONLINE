package vn.com.be_crm.application.lead.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** Output DTO cho LeadAttachment. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class LeadAttachmentResult {
    private Long id;
    private Long leadId;
    private String fileName;
    private String fileUrl;
    private Integer fileSize;
    private String mimeType;
    private Long uploadedBy;
    private LocalDateTime createdAt;
}
