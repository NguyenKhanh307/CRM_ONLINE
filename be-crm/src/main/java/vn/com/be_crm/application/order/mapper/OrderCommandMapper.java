package vn.com.be_crm.application.order.mapper;

import vn.com.be_crm.application.order.dto.*;
import vn.com.be_crm.domain.order.entity.Order;
import vn.com.be_crm.domain.order.enums.OrderStatus;
import vn.com.be_crm.domain.order.enums.OrderType;
import vn.com.be_crm.domain.order.enums.PaymentStatus;

import java.math.BigDecimal;

/** Chuyển đổi Command ↔ Order ↔ OrderResult. */
public class OrderCommandMapper {

    /**
     * Tạo Order từ CreateOrderCommand.
     * @param cmd command tạo mới @return domain entity
     */
    public static Order toEntity(CreateOrderCommand cmd) {
        return Order.builder()
                .code(cmd.getCode()).customerId(cmd.getCustomerId()).contactId(cmd.getContactId())
                .quotationId(cmd.getQuotationId()).opportunityId(cmd.getOpportunityId())
                .ownerId(cmd.getOwnerId()).executorUnitId(cmd.getExecutorUnitId()).warehouseId(cmd.getWarehouseId())
                .parentOrderId(cmd.getParentOrderId())
                .orderType(cmd.getOrderType() != null ? cmd.getOrderType() : OrderType.standard)
                .orderDate(cmd.getOrderDate())
                .currency(cmd.getCurrency() != null ? cmd.getCurrency() : "VND")
                .exchangeRate(cmd.getExchangeRate() != null ? cmd.getExchangeRate() : BigDecimal.ONE)
                .status(cmd.getStatus() != null ? cmd.getStatus() : OrderStatus.draft)
                .paymentStatus(cmd.getPaymentStatus() != null ? cmd.getPaymentStatus() : PaymentStatus.unpaid)
                .creditDays(cmd.getCreditDays()).paymentDueDate(cmd.getPaymentDueDate())
                .isInvoiced(cmd.getIsInvoiced() != null && cmd.getIsInvoiced())
                .receiverName(cmd.getReceiverName()).receiverPhone(cmd.getReceiverPhone())
                .subtotal(cmd.getSubtotal() != null ? cmd.getSubtotal() : BigDecimal.ZERO)
                .discount(cmd.getDiscount() != null ? cmd.getDiscount() : BigDecimal.ZERO)
                .tax(cmd.getTax() != null ? cmd.getTax() : BigDecimal.ZERO)
                .total(cmd.getTotal() != null ? cmd.getTotal() : BigDecimal.ZERO)
                .note(cmd.getNote()).build();
    }

    /**
     * Cập nhật Order từ UpdateOrderCommand.
     * @param cmd command cập nhật @param e entity hiện tại @return domain entity đã cập nhật
     */
    public static Order toEntity(UpdateOrderCommand cmd, Order e) {
        return Order.builder()
                .id(e.getId()).code(e.getCode()).orderType(e.getOrderType()).parentOrderId(e.getParentOrderId())
                .customerId(cmd.getCustomerId() != null ? cmd.getCustomerId() : e.getCustomerId())
                .contactId(cmd.getContactId() != null ? cmd.getContactId() : e.getContactId())
                .quotationId(cmd.getQuotationId() != null ? cmd.getQuotationId() : e.getQuotationId())
                .opportunityId(cmd.getOpportunityId() != null ? cmd.getOpportunityId() : e.getOpportunityId())
                .ownerId(cmd.getOwnerId() != null ? cmd.getOwnerId() : e.getOwnerId())
                .executorUnitId(cmd.getExecutorUnitId() != null ? cmd.getExecutorUnitId() : e.getExecutorUnitId())
                .warehouseId(cmd.getWarehouseId() != null ? cmd.getWarehouseId() : e.getWarehouseId())
                .orderDate(cmd.getOrderDate() != null ? cmd.getOrderDate() : e.getOrderDate())
                .currency(cmd.getCurrency() != null ? cmd.getCurrency() : e.getCurrency())
                .exchangeRate(cmd.getExchangeRate() != null ? cmd.getExchangeRate() : e.getExchangeRate())
                .status(cmd.getStatus() != null ? cmd.getStatus() : e.getStatus())
                .paymentStatus(cmd.getPaymentStatus() != null ? cmd.getPaymentStatus() : e.getPaymentStatus())
                .creditDays(cmd.getCreditDays() != null ? cmd.getCreditDays() : e.getCreditDays())
                .paymentDueDate(cmd.getPaymentDueDate() != null ? cmd.getPaymentDueDate() : e.getPaymentDueDate())
                .isInvoiced(cmd.getIsInvoiced() != null ? cmd.getIsInvoiced() : e.isInvoiced())
                .receiverName(cmd.getReceiverName() != null ? cmd.getReceiverName() : e.getReceiverName())
                .receiverPhone(cmd.getReceiverPhone() != null ? cmd.getReceiverPhone() : e.getReceiverPhone())
                .subtotal(cmd.getSubtotal() != null ? cmd.getSubtotal() : e.getSubtotal())
                .discount(cmd.getDiscount() != null ? cmd.getDiscount() : e.getDiscount())
                .tax(cmd.getTax() != null ? cmd.getTax() : e.getTax())
                .total(cmd.getTotal() != null ? cmd.getTotal() : e.getTotal())
                .note(cmd.getNote() != null ? cmd.getNote() : e.getNote())
                .createdAt(e.getCreatedAt()).build();
    }

    /**
     * Chuyển Order sang OrderResult.
     * @param e domain entity @return result DTO
     */
    public static OrderResult toResult(Order e) {
        return OrderResult.builder()
                .id(e.getId()).code(e.getCode()).customerId(e.getCustomerId()).contactId(e.getContactId())
                .quotationId(e.getQuotationId()).opportunityId(e.getOpportunityId())
                .ownerId(e.getOwnerId()).executorUnitId(e.getExecutorUnitId()).warehouseId(e.getWarehouseId())
                .parentOrderId(e.getParentOrderId()).orderType(e.getOrderType()).orderDate(e.getOrderDate())
                .currency(e.getCurrency()).exchangeRate(e.getExchangeRate())
                .status(e.getStatus()).paymentStatus(e.getPaymentStatus())
                .creditDays(e.getCreditDays()).paymentDueDate(e.getPaymentDueDate())
                .isInvoiced(e.isInvoiced()).receiverName(e.getReceiverName()).receiverPhone(e.getReceiverPhone())
                .subtotal(e.getSubtotal())
                .discount(e.getDiscount()).tax(e.getTax()).total(e.getTotal()).note(e.getNote())
                .createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt()).build();
    }

    private OrderCommandMapper() {}
}
