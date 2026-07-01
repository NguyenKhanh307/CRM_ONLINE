package vn.com.be_crm.infrastructure.service.mapper;

import org.springframework.stereotype.Component;
import vn.com.be_crm.domain.service.entity.TicketReturnItem;
import vn.com.be_crm.infrastructure.service.entity.TicketReturnItemHibernate;

import java.math.BigDecimal;

/** Chuyển đổi giữa TicketReturnItem domain entity ↔ TicketReturnItemHibernate. */
@Component
public class TicketReturnItemHibernateMapper {
    /** Chuyển domain entity sang Hibernate entity. @param d @return hibernate entity */
    public TicketReturnItemHibernate toHibernate(TicketReturnItem d) {
        TicketReturnItemHibernate h = new TicketReturnItemHibernate();
        h.setId(d.getId()); h.setTicketId(d.getTicketId()); h.setInvoiceItemId(d.getInvoiceItemId());
        h.setProductId(d.getProductId());
        h.setQuantity(d.getQuantity() != null ? d.getQuantity() : BigDecimal.ONE);
        h.setUnitPrice(d.getUnitPrice() != null ? d.getUnitPrice() : BigDecimal.ZERO);
        h.setAmount(d.getAmount() != null ? d.getAmount() : BigDecimal.ZERO);
        h.setReason(d.getReason()); h.setConditionNote(d.getConditionNote());
        return h;
    }
    /** Chuyển Hibernate entity sang domain entity. @param h @return domain entity */
    public TicketReturnItem toDomain(TicketReturnItemHibernate h) {
        return TicketReturnItem.builder()
                .id(h.getId()).ticketId(h.getTicketId()).invoiceItemId(h.getInvoiceItemId())
                .productId(h.getProductId()).quantity(h.getQuantity()).unitPrice(h.getUnitPrice())
                .amount(h.getAmount()).reason(h.getReason()).conditionNote(h.getConditionNote())
                .build();
    }
}
