package vn.com.be_crm.infrastructure.quotation.mapper;

import org.springframework.stereotype.Component;
import vn.com.be_crm.domain.quotation.entity.QuotationApproval;
import vn.com.be_crm.domain.quotation.enums.QuotationApprovalStatus;
import vn.com.be_crm.infrastructure.quotation.entity.QuotationApprovalHibernate;

/** Chuyển đổi giữa QuotationApproval domain entity ↔ QuotationApprovalHibernate. */
@Component
public class QuotationApprovalHibernateMapper {
    /** Chuyển domain entity sang Hibernate entity. @param d @return hibernate entity */
    public QuotationApprovalHibernate toHibernate(QuotationApproval d) {
        QuotationApprovalHibernate h = new QuotationApprovalHibernate();
        h.setId(d.getId()); h.setQuotationId(d.getQuotationId()); h.setApproverId(d.getApproverId());
        h.setLevel(d.getLevel() != null ? d.getLevel() : 1);
        h.setStatus(d.getStatus() != null ? d.getStatus() : QuotationApprovalStatus.pending);
        h.setComment(d.getComment()); h.setApprovedAt(d.getApprovedAt());
        return h;
    }
    /** Chuyển Hibernate entity sang domain entity. @param h @return domain entity */
    public QuotationApproval toDomain(QuotationApprovalHibernate h) {
        return QuotationApproval.builder()
                .id(h.getId()).quotationId(h.getQuotationId()).approverId(h.getApproverId())
                .level(h.getLevel()).status(h.getStatus()).comment(h.getComment())
                .approvedAt(h.getApprovedAt()).createdAt(h.getCreatedAt()).build();
    }
}
