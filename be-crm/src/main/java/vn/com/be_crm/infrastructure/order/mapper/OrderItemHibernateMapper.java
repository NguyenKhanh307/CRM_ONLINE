package vn.com.be_crm.infrastructure.order.mapper;

import org.springframework.stereotype.Component;
import vn.com.be_crm.domain.order.entity.OrderItem;
import vn.com.be_crm.infrastructure.order.entity.OrderItemHibernate;

import java.math.BigDecimal;

/** Chuyển đổi giữa OrderItem domain entity ↔ OrderItemHibernate. */
@Component
public class OrderItemHibernateMapper {
    /** Chuyển domain entity sang Hibernate entity. @param d @return hibernate entity */
    public OrderItemHibernate toHibernate(OrderItem d) {
        OrderItemHibernate h = new OrderItemHibernate();
        h.setId(d.getId()); h.setOrderId(d.getOrderId()); h.setProductId(d.getProductId());
        h.setWarehouseId(d.getWarehouseId());
        h.setQuantity(d.getQuantity() != null ? d.getQuantity() : BigDecimal.ONE);
        h.setUnitPrice(d.getUnitPrice() != null ? d.getUnitPrice() : BigDecimal.ZERO);
        h.setDiscount(d.getDiscount() != null ? d.getDiscount() : BigDecimal.ZERO);
        h.setTaxRate(d.getTaxRate() != null ? d.getTaxRate() : BigDecimal.ZERO);
        h.setAmount(d.getAmount() != null ? d.getAmount() : BigDecimal.ZERO);
        h.setNote(d.getNote());
        return h;
    }
    /** Chuyển Hibernate entity sang domain entity. @param h @return domain entity */
    public OrderItem toDomain(OrderItemHibernate h) {
        return OrderItem.builder()
                .id(h.getId()).orderId(h.getOrderId()).productId(h.getProductId()).warehouseId(h.getWarehouseId())
                .quantity(h.getQuantity()).unitPrice(h.getUnitPrice()).discount(h.getDiscount())
                .taxRate(h.getTaxRate()).amount(h.getAmount()).note(h.getNote()).build();
    }
}
