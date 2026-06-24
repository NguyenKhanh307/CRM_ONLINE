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
        // Khóa chính & các liên kết (khách hàng, liên hệ, báo giá, cơ hội, đơn cha...)
        h.setId(d.getId()); h.setCode(d.getCode()); h.setCustomerId(d.getCustomerId());
        h.setContactId(d.getContactId());
        h.setQuotationId(d.getQuotationId()); h.setOpportunityId(d.getOpportunityId());
        h.setOwnerId(d.getOwnerId());
        h.setExecutorUnitId(d.getExecutorUnitId());
        h.setParentOrderId(d.getParentOrderId());
        // Giá trị mặc định khi null (loại đơn=standard, tiền tệ=VND, tỷ giá=1, trạng thái=draft/unpaid)
        h.setOrderType(d.getOrderType() != null ? d.getOrderType() : OrderType.standard);
        h.setOrderDate(d.getOrderDate());
        h.setCurrency(d.getCurrency() != null ? d.getCurrency() : "VND");
        h.setExchangeRate(d.getExchangeRate() != null ? d.getExchangeRate() : BigDecimal.ONE);
        h.setStatus(d.getStatus() != null ? d.getStatus() : OrderStatus.draft);
        h.setPaymentStatus(d.getPaymentStatus() != null ? d.getPaymentStatus() : PaymentStatus.unpaid);
        h.setCreditDays(d.getCreditDays()); h.setPaymentDueDate(d.getPaymentDueDate());
        h.setInvoiced(d.isInvoiced());
        h.setReceiverName(d.getReceiverName()); h.setReceiverPhone(d.getReceiverPhone());
        // Các trường tiền: mặc định 0 nếu null để tránh NPE khi tính toán/lưu DB
        h.setSubtotal(d.getSubtotal() != null ? d.getSubtotal() : BigDecimal.ZERO);
        h.setDiscount(d.getDiscount() != null ? d.getDiscount() : BigDecimal.ZERO);
        h.setTax(d.getTax() != null ? d.getTax() : BigDecimal.ZERO);
        h.setTotal(d.getTotal() != null ? d.getTotal() : BigDecimal.ZERO);
        // Ghi chú & cờ xóa mềm
        h.setNote(d.getNote()); h.setDeletedAt(d.getDeletedAt());
        h.setDeletedBy(d.getDeletedBy()); h.setPurged(d.isPurged());
        return h;
    }
    /** Chuyển Hibernate entity sang domain entity. @param h @return domain entity */
    public Order toDomain(OrderHibernate h) {
        return Order.builder()
                .id(h.getId()).code(h.getCode()).customerId(h.getCustomerId()).contactId(h.getContactId())
                .quotationId(h.getQuotationId()).opportunityId(h.getOpportunityId())
                .ownerId(h.getOwnerId()).executorUnitId(h.getExecutorUnitId())
                .parentOrderId(h.getParentOrderId()).orderType(h.getOrderType()).orderDate(h.getOrderDate())
                .currency(h.getCurrency()).exchangeRate(h.getExchangeRate())
                .status(h.getStatus()).paymentStatus(h.getPaymentStatus())
                .creditDays(h.getCreditDays()).paymentDueDate(h.getPaymentDueDate())
                .isInvoiced(h.isInvoiced()).receiverName(h.getReceiverName()).receiverPhone(h.getReceiverPhone())
                .subtotal(h.getSubtotal())
                .discount(h.getDiscount()).tax(h.getTax()).total(h.getTotal()).note(h.getNote())
                .createdAt(h.getCreatedAt()).updatedAt(h.getUpdatedAt()).deletedAt(h.getDeletedAt())
                .deletedBy(h.getDeletedBy()).isPurged(h.isPurged()).build();
    }
}
