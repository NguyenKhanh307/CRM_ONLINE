package vn.com.be_crm.application.order.mapper;

import vn.com.be_crm.application.order.dto.*;
import vn.com.be_crm.domain.order.entity.OrderItem;

import java.math.BigDecimal;

/** Chuyển đổi Command ↔ OrderItem ↔ OrderItemResult. */
public class OrderItemCommandMapper {

    /**
     * Tạo OrderItem từ CreateOrderItemCommand.
     * @param cmd command tạo mới @return domain entity
     */
    public static OrderItem toEntity(CreateOrderItemCommand cmd) {
        return OrderItem.builder()
                .orderId(cmd.getOrderId()).productId(cmd.getProductId())
                .unit(cmd.getUnit())
                .quantity(cmd.getQuantity() != null ? cmd.getQuantity() : BigDecimal.ONE)
                .unitPrice(cmd.getUnitPrice() != null ? cmd.getUnitPrice() : BigDecimal.ZERO)
                .discount(cmd.getDiscount() != null ? cmd.getDiscount() : BigDecimal.ZERO)
                .taxRate(cmd.getTaxRate() != null ? cmd.getTaxRate() : BigDecimal.ZERO)
                .amount(cmd.getAmount() != null ? cmd.getAmount() : BigDecimal.ZERO)
                .note(cmd.getNote()).build();
    }

    /**
     * Cập nhật OrderItem từ UpdateOrderItemCommand.
     * @param cmd command cập nhật @param e entity hiện tại @return domain entity đã cập nhật
     */
    public static OrderItem toEntity(UpdateOrderItemCommand cmd, OrderItem e) {
        return OrderItem.builder()
                .id(e.getId()).orderId(e.getOrderId())
                .productId(cmd.getProductId() != null ? cmd.getProductId() : e.getProductId())
                .unit(cmd.getUnit() != null ? cmd.getUnit() : e.getUnit())
                .quantity(cmd.getQuantity() != null ? cmd.getQuantity() : e.getQuantity())
                .unitPrice(cmd.getUnitPrice() != null ? cmd.getUnitPrice() : e.getUnitPrice())
                .discount(cmd.getDiscount() != null ? cmd.getDiscount() : e.getDiscount())
                .taxRate(cmd.getTaxRate() != null ? cmd.getTaxRate() : e.getTaxRate())
                .amount(cmd.getAmount() != null ? cmd.getAmount() : e.getAmount())
                .note(cmd.getNote() != null ? cmd.getNote() : e.getNote()).build();
    }

    /**
     * Chuyển OrderItem sang OrderItemResult.
     * @param e domain entity @return result DTO
     */
    public static OrderItemResult toResult(OrderItem e) {
        return OrderItemResult.builder()
                .id(e.getId()).orderId(e.getOrderId()).productId(e.getProductId())
                .unit(e.getUnit())
                .quantity(e.getQuantity()).unitPrice(e.getUnitPrice()).discount(e.getDiscount())
                .taxRate(e.getTaxRate()).amount(e.getAmount()).note(e.getNote()).build();
    }

    private OrderItemCommandMapper() {}
}
