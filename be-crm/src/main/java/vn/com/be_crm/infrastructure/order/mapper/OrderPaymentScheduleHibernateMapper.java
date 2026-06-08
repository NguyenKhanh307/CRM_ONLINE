package vn.com.be_crm.infrastructure.order.mapper;

import org.springframework.stereotype.Component;
import vn.com.be_crm.domain.order.entity.OrderPaymentSchedule;
import vn.com.be_crm.domain.order.enums.PaymentScheduleStatus;
import vn.com.be_crm.infrastructure.order.entity.OrderPaymentScheduleHibernate;

import java.math.BigDecimal;

/** Chuyển đổi giữa OrderPaymentSchedule domain entity ↔ OrderPaymentScheduleHibernate. */
@Component
public class OrderPaymentScheduleHibernateMapper {
    /** Chuyển domain entity sang Hibernate entity. @param d @return hibernate entity */
    public OrderPaymentScheduleHibernate toHibernate(OrderPaymentSchedule d) {
        OrderPaymentScheduleHibernate h = new OrderPaymentScheduleHibernate();
        h.setId(d.getId()); h.setOrderId(d.getOrderId());
        h.setInstallmentNo(d.getInstallmentNo() != null ? d.getInstallmentNo() : 1);
        h.setDueDate(d.getDueDate());
        h.setAmount(d.getAmount() != null ? d.getAmount() : BigDecimal.ZERO);
        h.setPaidAmount(d.getPaidAmount() != null ? d.getPaidAmount() : BigDecimal.ZERO);
        h.setStatus(d.getStatus() != null ? d.getStatus() : PaymentScheduleStatus.pending);
        h.setPaidAt(d.getPaidAt()); h.setNote(d.getNote());
        return h;
    }
    /** Chuyển Hibernate entity sang domain entity. @param h @return domain entity */
    public OrderPaymentSchedule toDomain(OrderPaymentScheduleHibernate h) {
        return OrderPaymentSchedule.builder()
                .id(h.getId()).orderId(h.getOrderId()).installmentNo(h.getInstallmentNo())
                .dueDate(h.getDueDate()).amount(h.getAmount()).paidAmount(h.getPaidAmount())
                .status(h.getStatus()).paidAt(h.getPaidAt()).note(h.getNote()).build();
    }
}
