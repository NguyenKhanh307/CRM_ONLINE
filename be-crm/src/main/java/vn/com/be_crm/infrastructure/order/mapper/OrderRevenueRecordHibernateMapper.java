package vn.com.be_crm.infrastructure.order.mapper;

import org.springframework.stereotype.Component;
import vn.com.be_crm.domain.order.entity.OrderRevenueRecord;
import vn.com.be_crm.infrastructure.order.entity.OrderRevenueRecordHibernate;

import java.math.BigDecimal;

/** Chuyển đổi giữa OrderRevenueRecord domain entity ↔ OrderRevenueRecordHibernate. */
@Component
public class OrderRevenueRecordHibernateMapper {
    /** Chuyển domain entity sang Hibernate entity. @param d @return hibernate entity */
    public OrderRevenueRecordHibernate toHibernate(OrderRevenueRecord d) {
        OrderRevenueRecordHibernate h = new OrderRevenueRecordHibernate();
        h.setId(d.getId()); h.setOrderId(d.getOrderId()); h.setUserId(d.getUserId());
        h.setRevenueAmount(d.getRevenueAmount() != null ? d.getRevenueAmount() : BigDecimal.ZERO);
        h.setPercentage(d.getPercentage() != null ? d.getPercentage() : BigDecimal.ZERO);
        h.setNote(d.getNote());
        return h;
    }
    /** Chuyển Hibernate entity sang domain entity. @param h @return domain entity */
    public OrderRevenueRecord toDomain(OrderRevenueRecordHibernate h) {
        return OrderRevenueRecord.builder()
                .id(h.getId()).orderId(h.getOrderId()).userId(h.getUserId())
                .revenueAmount(h.getRevenueAmount()).percentage(h.getPercentage())
                .note(h.getNote()).createdAt(h.getCreatedAt()).build();
    }
}
