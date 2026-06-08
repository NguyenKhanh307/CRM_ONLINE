package vn.com.be_crm.application.order.mapper;

import vn.com.be_crm.application.order.dto.*;
import vn.com.be_crm.domain.order.entity.OrderPaymentSchedule;
import vn.com.be_crm.domain.order.enums.PaymentScheduleStatus;

import java.math.BigDecimal;

/** Chuyển đổi Command ↔ OrderPaymentSchedule ↔ OrderPaymentScheduleResult. */
public class OrderPaymentScheduleCommandMapper {

    /**
     * Tạo OrderPaymentSchedule từ CreateOrderPaymentScheduleCommand.
     * @param cmd command tạo mới @return domain entity
     */
    public static OrderPaymentSchedule toEntity(CreateOrderPaymentScheduleCommand cmd) {
        return OrderPaymentSchedule.builder()
                .orderId(cmd.getOrderId())
                .installmentNo(cmd.getInstallmentNo() != null ? cmd.getInstallmentNo() : 1)
                .dueDate(cmd.getDueDate())
                .amount(cmd.getAmount() != null ? cmd.getAmount() : BigDecimal.ZERO)
                .paidAmount(cmd.getPaidAmount() != null ? cmd.getPaidAmount() : BigDecimal.ZERO)
                .status(cmd.getStatus() != null ? cmd.getStatus() : PaymentScheduleStatus.pending)
                .paidAt(cmd.getPaidAt()).note(cmd.getNote()).build();
    }

    /**
     * Cập nhật OrderPaymentSchedule từ UpdateOrderPaymentScheduleCommand.
     * @param cmd command cập nhật @param e entity hiện tại @return domain entity đã cập nhật
     */
    public static OrderPaymentSchedule toEntity(UpdateOrderPaymentScheduleCommand cmd, OrderPaymentSchedule e) {
        return OrderPaymentSchedule.builder()
                .id(e.getId()).orderId(e.getOrderId()).installmentNo(e.getInstallmentNo())
                .dueDate(cmd.getDueDate() != null ? cmd.getDueDate() : e.getDueDate())
                .amount(cmd.getAmount() != null ? cmd.getAmount() : e.getAmount())
                .paidAmount(cmd.getPaidAmount() != null ? cmd.getPaidAmount() : e.getPaidAmount())
                .status(cmd.getStatus() != null ? cmd.getStatus() : e.getStatus())
                .paidAt(cmd.getPaidAt() != null ? cmd.getPaidAt() : e.getPaidAt())
                .note(cmd.getNote() != null ? cmd.getNote() : e.getNote()).build();
    }

    /**
     * Chuyển OrderPaymentSchedule sang OrderPaymentScheduleResult.
     * @param e domain entity @return result DTO
     */
    public static OrderPaymentScheduleResult toResult(OrderPaymentSchedule e) {
        return OrderPaymentScheduleResult.builder()
                .id(e.getId()).orderId(e.getOrderId()).installmentNo(e.getInstallmentNo())
                .dueDate(e.getDueDate()).amount(e.getAmount()).paidAmount(e.getPaidAmount())
                .status(e.getStatus()).paidAt(e.getPaidAt()).note(e.getNote()).build();
    }

    private OrderPaymentScheduleCommandMapper() {}
}
