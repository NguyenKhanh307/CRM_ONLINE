package vn.com.be_crm.infrastructure.order.mapper;

import org.springframework.stereotype.Component;
import vn.com.be_crm.domain.order.entity.Order;
import vn.com.be_crm.domain.order.enums.OrderStatus;
import vn.com.be_crm.infrastructure.order.entity.OrderHibernate;

import java.math.BigDecimal;

/** Chuyển đổi giữa Order domain entity ↔ OrderHibernate. */
@Component
public class OrderHibernateMapper {
    /** Chuyển domain entity sang Hibernate entity. @param d @return hibernate entity */
    public OrderHibernate toHibernate(Order d) {
        OrderHibernate h = new OrderHibernate();
        // Khóa chính & các liên kết (khách hàng, liên hệ, báo giá, cơ hội, chiến dịch)
        h.setId(d.getId()); h.setCode(d.getCode()); h.setCustomerId(d.getCustomerId());
        h.setContactId(d.getContactId());
        h.setQuotationId(d.getQuotationId()); h.setOpportunityId(d.getOpportunityId());
        h.setCampaignId(d.getCampaignId()); h.setOwnerId(d.getOwnerId());
        // Giá trị mặc định khi null (tiền tệ=VND, tỷ giá=1, trạng thái=draft)
        h.setOrderDate(d.getOrderDate());
        h.setDeliveryDate(d.getDeliveryDate());
        h.setCurrency(d.getCurrency() != null ? d.getCurrency() : "VND");
        h.setExchangeRate(d.getExchangeRate() != null ? d.getExchangeRate() : BigDecimal.ONE);
        h.setStatus(d.getStatus() != null ? d.getStatus() : OrderStatus.draft);
        h.setLocked(d.isLocked());
        h.setBillingAddress(d.getBillingAddress());
        h.setTaxCode(d.getTaxCode());
        // Các trường tiền: mặc định 0 nếu null để tránh NPE khi tính toán/lưu DB
        h.setSubtotal(d.getSubtotal() != null ? d.getSubtotal() : BigDecimal.ZERO);
        h.setDiscount(d.getDiscount() != null ? d.getDiscount() : BigDecimal.ZERO);
        h.setTax(d.getTax() != null ? d.getTax() : BigDecimal.ZERO);
        h.setTotal(d.getTotal() != null ? d.getTotal() : BigDecimal.ZERO);
        // Ghi chú & cờ xóa mềm
        h.setNote(d.getNote()); h.setDeletedAt(d.getDeletedAt());
        h.setDeletedBy(d.getDeletedBy()); h.setPurged(d.isPurged());
        return h;
    }
    /** Chuyển Hibernate entity sang domain entity. @param h @return domain entity */
    public Order toDomain(OrderHibernate h) {
        return Order.builder()
                .id(h.getId()).code(h.getCode()).customerId(h.getCustomerId()).contactId(h.getContactId())
                .quotationId(h.getQuotationId()).opportunityId(h.getOpportunityId())
                .campaignId(h.getCampaignId())
                .ownerId(h.getOwnerId()).orderDate(h.getOrderDate()).deliveryDate(h.getDeliveryDate())
                .currency(h.getCurrency()).exchangeRate(h.getExchangeRate())
                .status(h.getStatus())
                .isLocked(h.isLocked()).billingAddress(h.getBillingAddress()).taxCode(h.getTaxCode())
                .subtotal(h.getSubtotal())
                .discount(h.getDiscount()).tax(h.getTax()).total(h.getTotal()).note(h.getNote())
                .createdAt(h.getCreatedAt()).updatedAt(h.getUpdatedAt()).deletedAt(h.getDeletedAt())
                .deletedBy(h.getDeletedBy()).isPurged(h.isPurged()).build();
    }
}
