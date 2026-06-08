package vn.com.be_crm.domain.lead.repository;

import vn.com.be_crm.domain.lead.entity.LeadActivity;

import java.util.List;
import java.util.Optional;

/**
 * Port lưu trữ cho LeadActivity.
 */
public interface ILeadActivityRepository {

    /**
     * Lưu mới hoặc cập nhật hoạt động tiềm năng.
     * @param activity domain entity @return entity sau khi lưu
     */
    LeadActivity save(LeadActivity activity);

    /**
     * Tìm hoạt động theo ID.
     * @param id ID @return Optional
     */
    Optional<LeadActivity> findById(Long id);

    /**
     * Xóa hoạt động theo ID.
     * @param id ID cần xóa
     */
    void deleteById(Long id);

    /**
     * Lấy danh sách hoạt động theo leadId.
     * @param leadId ID tiềm năng @return danh sách
     */
    List<LeadActivity> findAllByLeadId(Long leadId);
}
