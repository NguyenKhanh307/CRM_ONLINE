package vn.com.be_crm.application.order.mapper;

import vn.com.be_crm.application.order.dto.*;
import vn.com.be_crm.domain.order.entity.Order;
import vn.com.be_crm.domain.order.enums.OrderStatus;

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
                .campaignId(cmd.getCampaignId()).ownerId(cmd.getOwnerId())
                .orderDate(cmd.getOrderDate()).deliveryDate(cmd.getDeliveryDate())
                .currency(cmd.getCurrency() != null ? cmd.getCurrency() : "VND")
                .exchangeRate(cmd.getExchangeRate() != null ? cmd.getExchangeRate() : BigDecimal.ONE)
                .status(OrderStatus.draft)
                .billingAddress(cmd.getBillingAddress()).taxCode(cmd.getTaxCode())
                .subtotal(cmd.getSubtotal() != null ? cmd.getSubtotal() : BigDecimal.ZERO)
                .discount(cmd.getDiscount() != null ? cmd.getDiscount() : BigDecimal.ZERO)
                .tax(cmd.getTax() != null ? cmd.getTax() : BigDecimal.ZERO)
                .total(cmd.getTotal() != null ? cmd.getTotal() : BigDecimal.ZERO)
                .note(cmd.getNote()).build();
    }

    /**
     * Cập nhật Order từ UpdateOrderCommand. Trạng thái & cờ khóa giữ nguyên (đổi qua hành động).
     * @param cmd command cập nhật @param e entity hiện tại @return domain entity đã cập nhật
     */
    public static Order toEntity(UpdateOrderCommand cmd, Order e) {
        return Order.builder()
                .id(e.getId()).code(e.getCode())
                .customerId(cmd.getCustomerId() != null ? cmd.getCustomerId() : e.getCustomerId())
                .contactId(cmd.getContactId() != null ? cmd.getContactId() : e.getContactId())
                .quotationId(cmd.getQuotationId() != null ? cmd.getQuotationId() : e.getQuotationId())
                .opportunityId(cmd.getOpportunityId() != null ? cmd.getOpportunityId() : e.getOpportunityId())
                .campaignId(cmd.getCampaignId() != null ? cmd.getCampaignId() : e.getCampaignId())
                .ownerId(cmd.getOwnerId() != null ? cmd.getOwnerId() : e.getOwnerId())
                .orderDate(cmd.getOrderDate() != null ? cmd.getOrderDate() : e.getOrderDate())
                .deliveryDate(cmd.getDeliveryDate() != null ? cmd.getDeliveryDate() : e.getDeliveryDate())
                .currency(cmd.getCurrency() != null ? cmd.getCurrency() : e.getCurrency())
                .exchangeRate(cmd.getExchangeRate() != null ? cmd.getExchangeRate() : e.getExchangeRate())
                .status(e.getStatus())
                .isLocked(e.isLocked())
                .billingAddress(cmd.getBillingAddress() != null ? cmd.getBillingAddress() : e.getBillingAddress())
                .taxCode(cmd.getTaxCode() != null ? cmd.getTaxCode() : e.getTaxCode())
                .subtotal(cmd.getSubtotal() != null ? cmd.getSubtotal() : e.getSubtotal())
                .discount(cmd.getDiscount() != null ? cmd.getDiscount() : e.getDiscount())
                .tax(cmd.getTax() != null ? cmd.getTax() : e.getTax())
                .total(cmd.getTotal() != null ? cmd.getTotal() : e.getTotal())
                .note(cmd.getNote() != null ? cmd.getNote() : e.getNote())
                .createdBy(e.getCreatedBy()).updatedBy(e.getUpdatedBy())
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
                .campaignId(e.getCampaignId())
                .ownerId(e.getOwnerId()).orderDate(e.getOrderDate()).deliveryDate(e.getDeliveryDate())
                .currency(e.getCurrency()).exchangeRate(e.getExchangeRate())
                .status(e.getStatus())
                .isLocked(e.isLocked()).billingAddress(e.getBillingAddress()).taxCode(e.getTaxCode())
                .subtotal(e.getSubtotal())
                .discount(e.getDiscount()).tax(e.getTax()).total(e.getTotal()).note(e.getNote())
                .createdBy(e.getCreatedBy()).updatedBy(e.getUpdatedBy())
                .createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt()).build();
    }

    private OrderCommandMapper() {}
}
