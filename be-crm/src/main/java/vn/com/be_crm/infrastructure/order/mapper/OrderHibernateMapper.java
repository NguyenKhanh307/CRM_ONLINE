package vn.com.be_crm.infrastructure.order.mapper;

import org.springframework.stereotype.Component;
import vn.com.be_crm.domain.order.entity.Order;
import vn.com.be_crm.domain.order.enums.OrderStatus;
import vn.com.be_crm.domain.order.enums.OrderType;
import vn.com.be_crm.domain.order.enums.PaymentStatus;
import vn.com.be_crm.infrastructure.order.entity.OrderHibernate;

import java.math.BigDecimal;

/** Chuyển đổi giữa Order domain entity ↔ OrderHibernate. */
@Component
public class OrderHibernateMapper {
    /** Chuyển domain entity sang Hibernate entity. @param d @return hibernate entity */
    public OrderHibernate toHibernate(Order d) {
        OrderHibernate h = new OrderHibernate();
        h.setId(d.getId()); h.setCode(d.getCode()); h.setCustomerId(d.getCustomerId());
        h.setContactId(d.getContactId()); h.setOwnerId(d.getOwnerId());
        h.setExecutorUnitId(d.getExecutorUnitId()); h.setWarehouseId(d.getWarehouseId());
        h.setParentOrderId(d.getParentOrderId());
        h.setOrderType(d.getOrderType() != null ? d.getOrderType() : OrderType.standard);
        h.setOrderDate(d.getOrderDate());
        h.setStatus(d.getStatus() != null ? d.getStatus() : OrderStatus.draft);
        h.setPaymentStatus(d.getPaymentStatus() != null ? d.getPaymentStatus() : PaymentStatus.unpaid);
        h.setSubtotal(d.getSubtotal() != null ? d.getSubtotal() : BigDecimal.ZERO);
        h.setDiscount(d.getDiscount() != null ? d.getDiscount() : BigDecimal.ZERO);
        h.setTax(d.getTax() != null ? d.getTax() : BigDecimal.ZERO);
        h.setTotal(d.getTotal() != null ? d.getTotal() : BigDecimal.ZERO);
        h.setNote(d.getNote()); h.setDeletedAt(d.getDeletedAt());
        return h;
    }
    /** Chuyển Hibernate entity sang domain entity. @param h @return domain entity */
    public Order toDomain(OrderHibernate h) {
        return Order.builder()
                .id(h.getId()).code(h.getCode()).customerId(h.getCustomerId()).contactId(h.getContactId())
                .ownerId(h.getOwnerId()).executorUnitId(h.getExecutorUnitId()).warehouseId(h.getWarehouseId())
                .parentOrderId(h.getParentOrderId()).orderType(h.getOrderType()).orderDate(h.getOrderDate())
                .status(h.getStatus()).paymentStatus(h.getPaymentStatus()).subtotal(h.getSubtotal())
                .discount(h.getDiscount()).tax(h.getTax()).total(h.getTotal()).note(h.getNote())
                .createdAt(h.getCreatedAt()).updatedAt(h.getUpdatedAt()).deletedAt(h.getDeletedAt()).build();
    }
}
