package vn.com.be_crm.infrastructure.service.mapper;

import org.springframework.stereotype.Component;
import vn.com.be_crm.domain.service.entity.TicketReturnItem;
import vn.com.be_crm.infrastructure.service.entity.TicketReturnItemHibernate;

import java.math.BigDecimal;

// chuyển đổi giữa TicketReturnItem domain entity <-> TicketReturnItemHibernate
@Component
public class TicketReturnItemHibernateMapper {

    public TicketReturnItemHibernate toHibernate(TicketReturnItem d) {
        TicketReturnItemHibernate h = new TicketReturnItemHibernate();
        h.setId(d.getId()); h.setTicketId(d.getTicketId()); h.setInvoiceItemId(d.getInvoiceItemId());
        h.setQuantity(d.getQuantity() != null ? d.getQuantity() : BigDecimal.ONE);
        h.setReason(d.getReason()); h.setConditionNote(d.getConditionNote());
        return h;
    }

    public TicketReturnItem toDomain(TicketReturnItemHibernate h) {
        return TicketReturnItem.builder()
                .id(h.getId()).ticketId(h.getTicketId()).invoiceItemId(h.getInvoiceItemId())
                .quantity(h.getQuantity())
                .reason(h.getReason()).conditionNote(h.getConditionNote())
                .build();
    }
}
