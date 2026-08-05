package vn.com.be_crm.infrastructure.invoice.mapper;

import org.springframework.stereotype.Component;
import vn.com.be_crm.domain.invoice.entity.InvoicePaymentSchedule;
import vn.com.be_crm.domain.invoice.enums.PaymentScheduleStatus;
import vn.com.be_crm.infrastructure.invoice.entity.InvoicePaymentScheduleHibernate;

import java.math.BigDecimal;

// chuyển đổi giữa InvoicePaymentSchedule domain entity <-> InvoicePaymentScheduleHibernate
@Component
public class InvoicePaymentScheduleHibernateMapper {

    public InvoicePaymentScheduleHibernate toHibernate(InvoicePaymentSchedule d) {
        InvoicePaymentScheduleHibernate h = new InvoicePaymentScheduleHibernate();
        h.setId(d.getId()); h.setInvoiceId(d.getInvoiceId());
        h.setInstallmentNo(d.getInstallmentNo() != null ? d.getInstallmentNo() : 1);
        h.setDueDate(d.getDueDate());
        h.setAmount(d.getAmount() != null ? d.getAmount() : BigDecimal.ZERO);
        h.setPaidAmount(d.getPaidAmount() != null ? d.getPaidAmount() : BigDecimal.ZERO);
        h.setStatus(d.getStatus() != null ? d.getStatus() : PaymentScheduleStatus.pending);
        h.setBankName(d.getBankName()); h.setBankAccount(d.getBankAccount());
        h.setNote(d.getNote());
        return h;
    }

    public InvoicePaymentSchedule toDomain(InvoicePaymentScheduleHibernate h) {
        return InvoicePaymentSchedule.builder()
                .id(h.getId()).invoiceId(h.getInvoiceId()).installmentNo(h.getInstallmentNo())
                .dueDate(h.getDueDate()).amount(h.getAmount()).paidAmount(h.getPaidAmount())
                .status(h.getStatus()).bankName(h.getBankName()).bankAccount(h.getBankAccount())
                .note(h.getNote()).build();
    }
}
