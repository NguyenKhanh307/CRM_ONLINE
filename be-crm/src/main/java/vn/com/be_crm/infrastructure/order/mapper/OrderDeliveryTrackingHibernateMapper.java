package vn.com.be_crm.infrastructure.order.mapper;

import org.springframework.stereotype.Component;
import vn.com.be_crm.domain.order.entity.OrderDeliveryTracking;
import vn.com.be_crm.domain.order.enums.DeliveryStatus;
import vn.com.be_crm.infrastructure.order.entity.OrderDeliveryTrackingHibernate;

/** Chuyển đổi giữa OrderDeliveryTracking domain entity ↔ OrderDeliveryTrackingHibernate. */
@Component
public class OrderDeliveryTrackingHibernateMapper {
    /** Chuyển domain entity sang Hibernate entity. @param d @return hibernate entity */
    public OrderDeliveryTrackingHibernate toHibernate(OrderDeliveryTracking d) {
        OrderDeliveryTrackingHibernate h = new OrderDeliveryTrackingHibernate();
        h.setId(d.getId()); h.setOrderId(d.getOrderId());
        h.setStatus(d.getStatus() != null ? d.getStatus() : DeliveryStatus.pending);
        h.setCarrier(d.getCarrier()); h.setTrackingNo(d.getTrackingNo());
        h.setShippedAt(d.getShippedAt()); h.setDeliveredAt(d.getDeliveredAt()); h.setNote(d.getNote());
        return h;
    }
    /** Chuyển Hibernate entity sang domain entity. @param h @return domain entity */
    public OrderDeliveryTracking toDomain(OrderDeliveryTrackingHibernate h) {
        return OrderDeliveryTracking.builder()
                .id(h.getId()).orderId(h.getOrderId()).status(h.getStatus()).carrier(h.getCarrier())
                .trackingNo(h.getTrackingNo()).shippedAt(h.getShippedAt()).deliveredAt(h.getDeliveredAt())
                .note(h.getNote()).createdAt(h.getCreatedAt()).updatedAt(h.getUpdatedAt()).build();
    }
}
