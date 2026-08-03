package vn.com.be_crm.infrastructure.lead.mapper;

import org.springframework.stereotype.Component;
import vn.com.be_crm.domain.lead.entity.Lead;
import vn.com.be_crm.domain.lead.enums.LeadStatus;
import vn.com.be_crm.core.audit.AuditStamper;
import vn.com.be_crm.infrastructure.lead.entity.LeadHibernate;

// chuyển đổi giữa Lead domain entity <-> LeadHibernate
@Component
public class LeadHibernateMapper {

    public LeadHibernate toHibernate(Lead d) {
        LeadHibernate h = new LeadHibernate();
        h.setId(d.getId()); h.setCode(d.getCode()); h.setName(d.getName());
        h.setCompanyName(d.getCompanyName()); h.setLeadType(d.getLeadType());
        h.setOwnerId(d.getOwnerId()); h.setCustomerId(d.getCustomerId()); h.setContactId(d.getContactId());
        h.setConvertedOpportunityId(d.getConvertedOpportunityId());
        h.setTitle(d.getTitle()); h.setDepartment(d.getDepartment());
        h.setTaxCode(d.getTaxCode()); h.setWebsite(d.getWebsite()); h.setIndustry(d.getIndustry());
        h.setSource(d.getSource()); h.setCampaignId(d.getCampaignId());
        h.setStatus(d.getStatus() != null ? d.getStatus() : LeadStatus.new_);
        h.setEstimatedValue(d.getEstimatedValue());
        h.setScore(d.getScore() != null ? d.getScore() : 0);
        h.setPhone(d.getPhone());
        h.setEmail(d.getEmail());
        h.setDoNotCall(d.isDoNotCall()); h.setDoNotEmail(d.isDoNotEmail());
        h.setNote(d.getNote()); h.setDeletedAt(d.getDeletedAt());
        h.setDeletedBy(d.getDeletedBy()); h.setPurged(d.isPurged());
        // đóng dấu người tạo/sửa ngay ở đây — cần cho body response của PUT (AuditInterceptor
        // chỉ chạy lúc flush, response được build trước đó)
        return AuditStamper.stamp(h, d.getCreatedBy(), d.getUpdatedBy());
    }

    public Lead toDomain(LeadHibernate h) {
        return Lead.builder()
                .id(h.getId()).code(h.getCode()).name(h.getName())
                .companyName(h.getCompanyName()).leadType(h.getLeadType())
                .ownerId(h.getOwnerId())
                .customerId(h.getCustomerId()).contactId(h.getContactId())
                .convertedOpportunityId(h.getConvertedOpportunityId())
                .title(h.getTitle()).department(h.getDepartment())
                .taxCode(h.getTaxCode()).website(h.getWebsite()).industry(h.getIndustry())
                .source(h.getSource()).campaignId(h.getCampaignId())
                .status(h.getStatus()).estimatedValue(h.getEstimatedValue()).score(h.getScore())
                .phone(h.getPhone())
                .email(h.getEmail())
                .doNotCall(h.isDoNotCall()).doNotEmail(h.isDoNotEmail())
                .note(h.getNote()).createdBy(h.getCreatedBy()).updatedBy(h.getUpdatedBy())
                .createdAt(h.getCreatedAt())
                .updatedAt(h.getUpdatedAt()).deletedAt(h.getDeletedAt())
                .deletedBy(h.getDeletedBy()).isPurged(h.isPurged()).build();
    }
}
