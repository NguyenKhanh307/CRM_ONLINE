package vn.com.be_crm.infrastructure.quotation.mapper;

import org.springframework.stereotype.Component;
import vn.com.be_crm.domain.quotation.entity.Quotation;
import vn.com.be_crm.domain.quotation.enums.QuotationStatus;
import vn.com.be_crm.core.audit.AuditStamper;
import vn.com.be_crm.infrastructure.quotation.entity.QuotationHibernate;

// chuyển đổi giữa Quotation domain entity <-> QuotationHibernate
@Component
public class QuotationHibernateMapper {

    public QuotationHibernate toHibernate(Quotation d) {
        QuotationHibernate h = new QuotationHibernate();
        h.setId(d.getId()); h.setCode(d.getCode()); h.setCustomerId(d.getCustomerId());
        h.setContactId(d.getContactId()); h.setOpportunityId(d.getOpportunityId());
        h.setPricePolicyId(d.getPricePolicyId());
        h.setPrimary(d.isPrimary()); h.setLocked(d.isLocked());
        h.setOwnerId(d.getOwnerId());
        h.setQuoteDate(d.getQuoteDate()); h.setValidUntil(d.getValidUntil());
        h.setStatus(d.getStatus() != null ? d.getStatus() : QuotationStatus.draft);
        h.setNote(d.getNote()); h.setDeletedAt(d.getDeletedAt());
        h.setDeletedBy(d.getDeletedBy()); h.setPurged(d.isPurged());
        h.setCustomerResponse(d.getCustomerResponse());
        h.setCustomerResponseNote(d.getCustomerResponseNote());
        // đóng dấu người tạo/người sửa ngay ở đây — cần cho body response của PUT
        return AuditStamper.stamp(h, d.getCreatedBy(), d.getUpdatedBy());
    }

    public Quotation toDomain(QuotationHibernate h) {
        return Quotation.builder()
                .id(h.getId()).code(h.getCode()).customerId(h.getCustomerId()).contactId(h.getContactId())
                .opportunityId(h.getOpportunityId())
                .pricePolicyId(h.getPricePolicyId()).isPrimary(h.isPrimary()).isLocked(h.isLocked())
                .ownerId(h.getOwnerId()).quoteDate(h.getQuoteDate()).validUntil(h.getValidUntil())
                .status(h.getStatus()).note(h.getNote())
                .customerResponse(h.getCustomerResponse())
                .customerResponseNote(h.getCustomerResponseNote())
                .createdBy(h.getCreatedBy()).updatedBy(h.getUpdatedBy())
                .createdAt(h.getCreatedAt()).updatedAt(h.getUpdatedAt()).deletedAt(h.getDeletedAt())
                .deletedBy(h.getDeletedBy()).isPurged(h.isPurged()).build();
    }
}
