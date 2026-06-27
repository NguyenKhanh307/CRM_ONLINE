package vn.com.be_crm.infrastructure.invoice.mapper;

import org.springframework.stereotype.Component;
import vn.com.be_crm.domain.invoice.entity.InvoicePaymentSchedule;
import vn.com.be_crm.domain.invoice.enums.PaymentScheduleStatus;
import vn.com.be_crm.infrastructure.invoice.entity.InvoicePaymentScheduleHibernate;

import java.math.BigDecimal;

/** Chuyển đổi giữa InvoicePaymentSchedule domain entity ↔ InvoicePaymentScheduleHibernate. */
@Component
public class InvoicePaymentScheduleHibernateMapper {
    /** Chuyển domain entity sang Hibernate entity. @param d @return hibernate entity */
    public InvoicePaymentScheduleHibernate toHibernate(InvoicePaymentSchedule d) {
        InvoicePaymentScheduleHibernate h = new InvoicePaymentScheduleHibernate();
        h.setId(d.getId()); h.setInvoiceId(d.getInvoiceId());
        h.setInstallmentNo(d.getInstallmentNo() != null ? d.getInstallmentNo() : 1);
        h.setDueDate(d.getDueDate());
        h.setAmount(d.getAmount() != null ? d.getAmount() : BigDecimal.ZERO);
        h.setPaidAmount(d.getPaidAmount() != null ? d.getPaidAmount() : BigDecimal.ZERO);
        h.setStatus(d.getStatus() != null ? d.getStatus() : PaymentScheduleStatus.pending);
        h.setPaidAt(d.getPaidAt()); h.setNote(d.getNote());
        return h;
    }
    /** Chuyển Hibernate entity sang domain entity. @param h @return domain entity */
    public InvoicePaymentSchedule toDomain(InvoicePaymentScheduleHibernate h) {
        return InvoicePaymentSchedule.builder()
                .id(h.getId()).invoiceId(h.getInvoiceId()).installmentNo(h.getInstallmentNo())
                .dueDate(h.getDueDate()).amount(h.getAmount()).paidAmount(h.getPaidAmount())
                .status(h.getStatus()).paidAt(h.getPaidAt()).note(h.getNote()).build();
    }
}
