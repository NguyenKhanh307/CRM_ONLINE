package vn.com.be_crm.infrastructure.order.mapper;

import org.springframework.stereotype.Component;
import vn.com.be_crm.domain.order.entity.OrderItem;
import vn.com.be_crm.infrastructure.order.entity.OrderItemHibernate;

import java.math.BigDecimal;

// chuyển đổi giữa OrderItem domain entity <-> OrderItemHibernate
@Component
public class OrderItemHibernateMapper {

    public OrderItemHibernate toHibernate(OrderItem d) {
        OrderItemHibernate h = new OrderItemHibernate();
        h.setId(d.getId()); h.setOrderId(d.getOrderId()); h.setProductId(d.getProductId());
        h.setUnit(d.getUnit());
        h.setQuantity(d.getQuantity() != null ? d.getQuantity() : BigDecimal.ONE);
        h.setUnitPrice(d.getUnitPrice() != null ? d.getUnitPrice() : BigDecimal.ZERO);
        h.setDiscount(d.getDiscount() != null ? d.getDiscount() : BigDecimal.ZERO);
        h.setTaxRate(d.getTaxRate() != null ? d.getTaxRate() : BigDecimal.ZERO);
        h.setNote(d.getNote());
        return h;
    }

    public OrderItem toDomain(OrderItemHibernate h) {
        return OrderItem.builder()
                .id(h.getId()).orderId(h.getOrderId()).productId(h.getProductId())
                .unit(h.getUnit())
                .quantity(h.getQuantity()).unitPrice(h.getUnitPrice()).discount(h.getDiscount())
                .taxRate(h.getTaxRate()).note(h.getNote()).build();
    }
}
