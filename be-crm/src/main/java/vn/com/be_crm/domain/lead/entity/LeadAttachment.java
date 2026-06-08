package vn.com.be_crm.domain.lead.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Domain entity đại diện cho tệp đính kèm tiềm năng.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeadAttachment {
    private Long id;
    private Long leadId;
    private String fileName;
    private String fileUrl;
    private Integer fileSize;
    private String mimeType;
    private Long uploadedBy;
    private LocalDateTime createdAt;
}
