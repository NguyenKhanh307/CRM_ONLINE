package vn.com.be_crm.application.order.mapper;

import vn.com.be_crm.application.order.dto.*;
import vn.com.be_crm.domain.order.entity.OrderRevenueRecord;

import java.math.BigDecimal;

/** Chuyển đổi Command ↔ OrderRevenueRecord ↔ OrderRevenueRecordResult. */
public class OrderRevenueRecordCommandMapper {

    /**
     * Tạo OrderRevenueRecord từ CreateOrderRevenueRecordCommand.
     * @param cmd command tạo mới @return domain entity
     */
    public static OrderRevenueRecord toEntity(CreateOrderRevenueRecordCommand cmd) {
        return OrderRevenueRecord.builder()
                .orderId(cmd.getOrderId()).userId(cmd.getUserId())
                .revenueAmount(cmd.getRevenueAmount() != null ? cmd.getRevenueAmount() : BigDecimal.ZERO)
                .percentage(cmd.getPercentage() != null ? cmd.getPercentage() : BigDecimal.ZERO)
                .note(cmd.getNote()).build();
    }

    /**
     * Cập nhật OrderRevenueRecord từ UpdateOrderRevenueRecordCommand.
     * @param cmd command cập nhật @param e entity hiện tại @return domain entity đã cập nhật
     */
    public static OrderRevenueRecord toEntity(UpdateOrderRevenueRecordCommand cmd, OrderRevenueRecord e) {
        return OrderRevenueRecord.builder()
                .id(e.getId()).orderId(e.getOrderId()).userId(e.getUserId())
                .revenueAmount(cmd.getRevenueAmount() != null ? cmd.getRevenueAmount() : e.getRevenueAmount())
                .percentage(cmd.getPercentage() != null ? cmd.getPercentage() : e.getPercentage())
                .note(cmd.getNote() != null ? cmd.getNote() : e.getNote())
                .createdAt(e.getCreatedAt()).build();
    }

    /**
     * Chuyển OrderRevenueRecord sang OrderRevenueRecordResult.
     * @param e domain entity @return result DTO
     */
    public static OrderRevenueRecordResult toResult(OrderRevenueRecord e) {
        return OrderRevenueRecordResult.builder()
                .id(e.getId()).orderId(e.getOrderId()).userId(e.getUserId())
                .revenueAmount(e.getRevenueAmount()).percentage(e.getPercentage())
                .note(e.getNote()).createdAt(e.getCreatedAt()).build();
    }

    private OrderRevenueRecordCommandMapper() {}
}
