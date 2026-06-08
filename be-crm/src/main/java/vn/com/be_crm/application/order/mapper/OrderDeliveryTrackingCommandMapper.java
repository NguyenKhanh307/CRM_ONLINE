package vn.com.be_crm.application.order.mapper;

import vn.com.be_crm.application.order.dto.*;
import vn.com.be_crm.domain.order.entity.OrderDeliveryTracking;
import vn.com.be_crm.domain.order.enums.DeliveryStatus;

/** Chuyển đổi Command ↔ OrderDeliveryTracking ↔ OrderDeliveryTrackingResult. */
public class OrderDeliveryTrackingCommandMapper {

    /**
     * Tạo OrderDeliveryTracking từ CreateOrderDeliveryTrackingCommand.
     * @param cmd command tạo mới @return domain entity
     */
    public static OrderDeliveryTracking toEntity(CreateOrderDeliveryTrackingCommand cmd) {
        return OrderDeliveryTracking.builder()
                .orderId(cmd.getOrderId())
                .status(cmd.getStatus() != null ? cmd.getStatus() : DeliveryStatus.pending)
                .carrier(cmd.getCarrier()).trackingNo(cmd.getTrackingNo())
                .shippedAt(cmd.getShippedAt()).deliveredAt(cmd.getDeliveredAt()).note(cmd.getNote()).build();
    }

    /**
     * Cập nhật OrderDeliveryTracking từ UpdateOrderDeliveryTrackingCommand.
     * @param cmd command cập nhật @param e entity hiện tại @return domain entity đã cập nhật
     */
    public static OrderDeliveryTracking toEntity(UpdateOrderDeliveryTrackingCommand cmd, OrderDeliveryTracking e) {
        return OrderDeliveryTracking.builder()
                .id(e.getId()).orderId(e.getOrderId())
                .status(cmd.getStatus() != null ? cmd.getStatus() : e.getStatus())
                .carrier(cmd.getCarrier() != null ? cmd.getCarrier() : e.getCarrier())
                .trackingNo(cmd.getTrackingNo() != null ? cmd.getTrackingNo() : e.getTrackingNo())
                .shippedAt(cmd.getShippedAt() != null ? cmd.getShippedAt() : e.getShippedAt())
                .deliveredAt(cmd.getDeliveredAt() != null ? cmd.getDeliveredAt() : e.getDeliveredAt())
                .note(cmd.getNote() != null ? cmd.getNote() : e.getNote())
                .createdAt(e.getCreatedAt()).build();
    }

    /**
     * Chuyển OrderDeliveryTracking sang OrderDeliveryTrackingResult.
     * @param e domain entity @return result DTO
     */
    public static OrderDeliveryTrackingResult toResult(OrderDeliveryTracking e) {
        return OrderDeliveryTrackingResult.builder()
                .id(e.getId()).orderId(e.getOrderId()).status(e.getStatus()).carrier(e.getCarrier())
                .trackingNo(e.getTrackingNo()).shippedAt(e.getShippedAt()).deliveredAt(e.getDeliveredAt())
                .note(e.getNote()).createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt()).build();
    }

    private OrderDeliveryTrackingCommandMapper() {}
}
