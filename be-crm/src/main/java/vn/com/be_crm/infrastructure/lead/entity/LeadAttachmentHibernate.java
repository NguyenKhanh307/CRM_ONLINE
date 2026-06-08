package vn.com.be_crm.infrastructure.lead.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Hibernate entity ánh xạ bảng lead_attachments.
 */
@Entity
@Table(name = "lead_attachments")
@Getter @Setter @NoArgsConstructor
public class LeadAttachmentHibernate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "lead_id", nullable = false)
    private Long leadId;
    @Column(name = "file_name", nullable = false, length = 30)
    private String fileName;
    @Column(name = "file_url", nullable = false, length = 255)
    private String fileUrl;
    @Column(name = "file_size")
    private Integer fileSize;
    @Column(name = "mime_type", length = 100)
    private String mimeType;
    @Column(name = "uploaded_by")
    private Long uploadedBy;
    @CreationTimestamp @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
