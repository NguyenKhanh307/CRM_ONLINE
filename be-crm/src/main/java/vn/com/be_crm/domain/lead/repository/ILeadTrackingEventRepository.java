package vn.com.be_crm.domain.lead.repository;

import vn.com.be_crm.domain.lead.entity.LeadTrackingEvent;

/**
 * Port lưu trữ cho LeadTrackingEvent (lịch sử web tracking).
 */
public interface ILeadTrackingEventRepository {

    /**
     * Lưu một sự kiện tracking.
     * @param event domain entity @return entity sau khi lưu
     */
    LeadTrackingEvent save(LeadTrackingEvent event);
}
