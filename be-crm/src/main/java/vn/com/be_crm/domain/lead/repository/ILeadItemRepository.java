package vn.com.be_crm.domain.lead.repository;

import vn.com.be_crm.domain.lead.entity.LeadItem;

import java.util.List;

// port lưu trữ cho LeadItem (sản phẩm quan tâm của tiềm năng)
public interface ILeadItemRepository {

    LeadItem save(LeadItem item);

    List<LeadItem> findAllByLeadId(Long leadId);
}
