package vn.com.be_crm.application.order.mapper;

import vn.com.be_crm.application.order.dto.*;
import vn.com.be_crm.domain.order.entity.Order;
import vn.com.be_crm.domain.order.enums.OrderStatus;

// chuyển đổi Command <-> Order <-> OrderResult. customerId/contactId/opportunityId/campaignId/
// currency/exchangeRate/billingAddress/taxCode/subtotal/discount/tax/total không còn trên Order
// — mọi liên kết tra qua quotationId, tổng tiền tính từ dòng hàng tại thời điểm đọc.
public class OrderCommandMapper {

    public static Order toEntity(CreateOrderCommand cmd) {
        return Order.builder()
                .code(cmd.getCode()).quotationId(cmd.getQuotationId()).ownerId(cmd.getOwnerId())
                .orderDate(cmd.getOrderDate()).deliveryDate(cmd.getDeliveryDate())
                .status(OrderStatus.draft)
                .note(cmd.getNote()).build();
    }

    // trạng thái & cờ khóa giữ nguyên (đổi qua hành động)
    public static Order toEntity(UpdateOrderCommand cmd, Order e) {
        return Order.builder()
                .id(e.getId()).code(e.getCode())
                .quotationId(cmd.getQuotationId() != null ? cmd.getQuotationId() : e.getQuotationId())
                .ownerId(cmd.getOwnerId() != null ? cmd.getOwnerId() : e.getOwnerId())
                .orderDate(cmd.getOrderDate() != null ? cmd.getOrderDate() : e.getOrderDate())
                .deliveryDate(cmd.getDeliveryDate() != null ? cmd.getDeliveryDate() : e.getDeliveryDate())
                .status(e.getStatus())
                .isLocked(e.isLocked())
                .note(cmd.getNote() != null ? cmd.getNote() : e.getNote())
                .createdBy(e.getCreatedBy()).updatedBy(e.getUpdatedBy())
                .createdAt(e.getCreatedAt()).build();
    }

    public static OrderResult toResult(Order e) {
        return OrderResult.builder()
                .id(e.getId()).code(e.getCode())
                .quotationId(e.getQuotationId())
                .ownerId(e.getOwnerId()).orderDate(e.getOrderDate()).deliveryDate(e.getDeliveryDate())
                .status(e.getStatus())
                .isLocked(e.isLocked()).note(e.getNote())
                .createdBy(e.getCreatedBy()).updatedBy(e.getUpdatedBy())
                .createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt()).build();
    }

    private OrderCommandMapper() {}
}
