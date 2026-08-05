package vn.com.be_crm.infrastructure.order.mapper;

import org.springframework.stereotype.Component;
import vn.com.be_crm.domain.order.entity.Order;
import vn.com.be_crm.domain.order.enums.OrderStatus;
import vn.com.be_crm.core.audit.AuditStamper;
import vn.com.be_crm.infrastructure.order.entity.OrderHibernate;

// chuyển đổi giữa Order domain entity <-> OrderHibernate
@Component
public class OrderHibernateMapper {

    public OrderHibernate toHibernate(Order d) {
        OrderHibernate h = new OrderHibernate();
        h.setId(d.getId()); h.setCode(d.getCode());
        h.setQuotationId(d.getQuotationId()); h.setOwnerId(d.getOwnerId());
        h.setOrderDate(d.getOrderDate());
        h.setDeliveryDate(d.getDeliveryDate());
        h.setStatus(d.getStatus() != null ? d.getStatus() : OrderStatus.draft);
        h.setLocked(d.isLocked());
        h.setNote(d.getNote()); h.setDeletedAt(d.getDeletedAt());
        h.setDeletedBy(d.getDeletedBy()); h.setPurged(d.isPurged());
        // đóng dấu người tạo/người sửa ngay ở đây — cần cho body response của PUT
        return AuditStamper.stamp(h, d.getCreatedBy(), d.getUpdatedBy());
    }

    public Order toDomain(OrderHibernate h) {
        return Order.builder()
                .id(h.getId()).code(h.getCode())
                .quotationId(h.getQuotationId())
                .ownerId(h.getOwnerId()).orderDate(h.getOrderDate()).deliveryDate(h.getDeliveryDate())
                .status(h.getStatus())
                .isLocked(h.isLocked()).note(h.getNote())
                .createdBy(h.getCreatedBy()).updatedBy(h.getUpdatedBy())
                .createdAt(h.getCreatedAt()).updatedAt(h.getUpdatedAt()).deletedAt(h.getDeletedAt())
                .deletedBy(h.getDeletedBy()).isPurged(h.isPurged()).build();
    }
}
