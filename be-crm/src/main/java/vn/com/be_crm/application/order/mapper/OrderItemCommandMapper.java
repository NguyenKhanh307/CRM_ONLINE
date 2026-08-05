package vn.com.be_crm.application.order.mapper;

import vn.com.be_crm.application.order.dto.*;
import vn.com.be_crm.core.util.LineItemTotals;
import vn.com.be_crm.domain.order.entity.OrderItem;

import java.math.BigDecimal;

// chuyển đổi Command <-> OrderItem <-> OrderItemResult. "amount" không lưu DB — tính bằng
// LineItemTotals ngay khi build Result.
public class OrderItemCommandMapper {

    public static OrderItem toEntity(CreateOrderItemCommand cmd) {
        return OrderItem.builder()
                .orderId(cmd.getOrderId()).productId(cmd.getProductId())
                .unit(cmd.getUnit())
                .quantity(cmd.getQuantity() != null ? cmd.getQuantity() : BigDecimal.ONE)
                .unitPrice(cmd.getUnitPrice() != null ? cmd.getUnitPrice() : BigDecimal.ZERO)
                .discount(cmd.getDiscount() != null ? cmd.getDiscount() : BigDecimal.ZERO)
                .taxRate(cmd.getTaxRate() != null ? cmd.getTaxRate() : BigDecimal.ZERO)
                .note(cmd.getNote()).build();
    }

    public static OrderItem toEntity(UpdateOrderItemCommand cmd, OrderItem e) {
        return OrderItem.builder()
                .id(e.getId()).orderId(e.getOrderId())
                .productId(cmd.getProductId() != null ? cmd.getProductId() : e.getProductId())
                .unit(cmd.getUnit() != null ? cmd.getUnit() : e.getUnit())
                .quantity(cmd.getQuantity() != null ? cmd.getQuantity() : e.getQuantity())
                .unitPrice(cmd.getUnitPrice() != null ? cmd.getUnitPrice() : e.getUnitPrice())
                .discount(cmd.getDiscount() != null ? cmd.getDiscount() : e.getDiscount())
                .taxRate(cmd.getTaxRate() != null ? cmd.getTaxRate() : e.getTaxRate())
                .note(cmd.getNote() != null ? cmd.getNote() : e.getNote()).build();
    }

    public static OrderItemResult toResult(OrderItem e) {
        return OrderItemResult.builder()
                .id(e.getId()).orderId(e.getOrderId()).productId(e.getProductId())
                .unit(e.getUnit())
                .quantity(e.getQuantity()).unitPrice(e.getUnitPrice()).discount(e.getDiscount())
                .taxRate(e.getTaxRate())
                .amount(LineItemTotals.lineAmount(e.getQuantity(), e.getUnitPrice(), e.getDiscount(), e.getTaxRate()))
                .note(e.getNote()).build();
    }

    private OrderItemCommandMapper() {}
}
