package vn.com.be_crm.domain.lead.repository;

import vn.com.be_crm.domain.lead.entity.LeadTrackingEvent;

// port lưu trữ cho LeadTrackingEvent (lịch sử web tracking)
public interface ILeadTrackingEventRepository {

    LeadTrackingEvent save(LeadTrackingEvent event);
}
