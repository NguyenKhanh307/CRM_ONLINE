package vn.com.be_crm.infrastructure.service.mapper;

import org.springframework.stereotype.Component;
import vn.com.be_crm.domain.service.entity.Ticket;
import vn.com.be_crm.domain.service.enums.TicketChannel;
import vn.com.be_crm.domain.service.enums.TicketPriority;
import vn.com.be_crm.domain.service.enums.TicketStatus;
import vn.com.be_crm.domain.service.enums.TicketType;
import vn.com.be_crm.core.audit.AuditStamper;
import vn.com.be_crm.infrastructure.service.entity.TicketHibernate;

/** Chuyển đổi giữa Ticket domain entity ↔ TicketHibernate. */
@Component
public class TicketHibernateMapper {
    /** Chuyển domain entity sang Hibernate entity. @param d @return hibernate entity */
    public TicketHibernate toHibernate(Ticket d) {
        TicketHibernate h = new TicketHibernate();
        h.setId(d.getId()); h.setCode(d.getCode());
        h.setType(d.getType() != null ? d.getType() : TicketType.support);
        h.setSubject(d.getSubject()); h.setDescription(d.getDescription());
        h.setCustomerId(d.getCustomerId()); h.setContactId(d.getContactId());
        h.setInvoiceId(d.getInvoiceId()); h.setProductId(d.getProductId());
        h.setChannel(d.getChannel() != null ? d.getChannel() : TicketChannel.web);
        h.setPriority(d.getPriority() != null ? d.getPriority() : TicketPriority.medium);
        h.setStatus(d.getStatus() != null ? d.getStatus() : TicketStatus.new_);
        h.setReason(d.getReason()); h.setResolutionType(d.getResolutionType());
        h.setResolutionNote(d.getResolutionNote()); h.setAssignedUserId(d.getAssignedUserId());
        h.setSlaPolicyId(d.getSlaPolicyId()); h.setFirstResponseAt(d.getFirstResponseAt());
        h.setSlaDueAt(d.getSlaDueAt()); h.setResolvedAt(d.getResolvedAt()); h.setClosedAt(d.getClosedAt());
        h.setSatisfactionScore(d.getSatisfactionScore()); h.setSatisfactionComment(d.getSatisfactionComment());
        h.setDeletedAt(d.getDeletedAt()); h.setDeletedBy(d.getDeletedBy()); h.setPurged(d.isPurged());
        // Đóng dấu người tạo/người sửa (AuditStamper: cần cho body response của PUT)
        return AuditStamper.stamp(h, d.getCreatedBy(), d.getUpdatedBy());
    }
    /** Chuyển Hibernate entity sang domain entity. @param h @return domain entity */
    public Ticket toDomain(TicketHibernate h) {
        return Ticket.builder()
                .id(h.getId()).code(h.getCode()).type(h.getType()).subject(h.getSubject())
                .description(h.getDescription()).customerId(h.getCustomerId()).contactId(h.getContactId())
                .invoiceId(h.getInvoiceId()).productId(h.getProductId()).channel(h.getChannel())
                .priority(h.getPriority()).status(h.getStatus()).reason(h.getReason())
                .resolutionType(h.getResolutionType()).resolutionNote(h.getResolutionNote())
                .assignedUserId(h.getAssignedUserId()).slaPolicyId(h.getSlaPolicyId())
                .firstResponseAt(h.getFirstResponseAt()).slaDueAt(h.getSlaDueAt())
                .resolvedAt(h.getResolvedAt()).closedAt(h.getClosedAt())
                .satisfactionScore(h.getSatisfactionScore()).satisfactionComment(h.getSatisfactionComment())
                .createdBy(h.getCreatedBy()).updatedBy(h.getUpdatedBy())
                .createdAt(h.getCreatedAt()).updatedAt(h.getUpdatedAt())
                .deletedAt(h.getDeletedAt()).deletedBy(h.getDeletedBy()).isPurged(h.isPurged())
                .build();
    }
}
