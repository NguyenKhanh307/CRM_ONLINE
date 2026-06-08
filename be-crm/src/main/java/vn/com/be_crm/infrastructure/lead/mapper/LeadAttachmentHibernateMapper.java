package vn.com.be_crm.infrastructure.lead.mapper;

import org.springframework.stereotype.Component;
import vn.com.be_crm.domain.lead.entity.LeadAttachment;
import vn.com.be_crm.infrastructure.lead.entity.LeadAttachmentHibernate;

/** Chuyển đổi giữa LeadAttachment domain entity ↔ LeadAttachmentHibernate. */
@Component
public class LeadAttachmentHibernateMapper {

    /**
     * Chuyển domain entity sang Hibernate entity.
     * @param d domain entity @return hibernate entity
     */
    public LeadAttachmentHibernate toHibernate(LeadAttachment d) {
        LeadAttachmentHibernate h = new LeadAttachmentHibernate();
        h.setId(d.getId()); h.setLeadId(d.getLeadId()); h.setFileName(d.getFileName());
        h.setFileUrl(d.getFileUrl()); h.setFileSize(d.getFileSize()); h.setMimeType(d.getMimeType());
        h.setUploadedBy(d.getUploadedBy());
        return h;
    }

    /**
     * Chuyển Hibernate entity sang domain entity.
     * @param h hibernate entity @return domain entity
     */
    public LeadAttachment toDomain(LeadAttachmentHibernate h) {
        return LeadAttachment.builder()
                .id(h.getId()).leadId(h.getLeadId()).fileName(h.getFileName()).fileUrl(h.getFileUrl())
                .fileSize(h.getFileSize()).mimeType(h.getMimeType()).uploadedBy(h.getUploadedBy())
                .createdAt(h.getCreatedAt()).build();
    }
}
