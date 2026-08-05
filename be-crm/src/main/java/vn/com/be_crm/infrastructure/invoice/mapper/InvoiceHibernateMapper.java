package vn.com.be_crm.infrastructure.invoice.mapper;

import org.springframework.stereotype.Component;
import vn.com.be_crm.domain.invoice.entity.Invoice;
import vn.com.be_crm.domain.invoice.enums.InvoiceStatus;
import vn.com.be_crm.domain.invoice.enums.PaymentStatus;
import vn.com.be_crm.core.audit.AuditStamper;
import vn.com.be_crm.infrastructure.invoice.entity.InvoiceHibernate;

// chuyển đổi giữa Invoice domain entity <-> InvoiceHibernate
@Component
public class InvoiceHibernateMapper {

    public InvoiceHibernate toHibernate(Invoice d) {
        InvoiceHibernate h = new InvoiceHibernate();
        h.setId(d.getId()); h.setCode(d.getCode());
        h.setOrderId(d.getOrderId());
        h.setOwnerId(d.getOwnerId());
        h.setInvoiceDate(d.getInvoiceDate());
        h.setDueDate(d.getDueDate());
        h.setStatus(d.getStatus() != null ? d.getStatus() : InvoiceStatus.draft);
        h.setPaymentStatus(d.getPaymentStatus() != null ? d.getPaymentStatus() : PaymentStatus.unpaid);
        h.setLocked(d.isLocked());
        h.setNote(d.getNote()); h.setDeletedAt(d.getDeletedAt());
        h.setDeletedBy(d.getDeletedBy()); h.setPurged(d.isPurged());
        // đóng dấu người tạo/người sửa ngay ở đây — cần cho body response của PUT
        return AuditStamper.stamp(h, d.getCreatedBy(), d.getUpdatedBy());
    }

    public Invoice toDomain(InvoiceHibernate h) {
        return Invoice.builder()
                .id(h.getId()).code(h.getCode())
                .orderId(h.getOrderId())
                .ownerId(h.getOwnerId()).invoiceDate(h.getInvoiceDate()).dueDate(h.getDueDate())
                .status(h.getStatus()).paymentStatus(h.getPaymentStatus())
                .isLocked(h.isLocked()).note(h.getNote())
                .createdBy(h.getCreatedBy()).updatedBy(h.getUpdatedBy())
                .createdAt(h.getCreatedAt()).updatedAt(h.getUpdatedAt()).deletedAt(h.getDeletedAt())
                .deletedBy(h.getDeletedBy()).isPurged(h.isPurged()).build();
    }
}
