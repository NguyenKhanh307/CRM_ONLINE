package vn.com.be_crm.domain.lead.repository;

import vn.com.be_crm.domain.lead.entity.LeadAttachment;

import java.util.List;
import java.util.Optional;

/**
 * Port lưu trữ cho LeadAttachment.
 */
public interface ILeadAttachmentRepository {

    /**
     * Lưu mới hoặc cập nhật tệp đính kèm.
     * @param attachment domain entity @return entity sau khi lưu
     */
    LeadAttachment save(LeadAttachment attachment);

    /**
     * Tìm tệp đính kèm theo ID.
     * @param id ID @return Optional
     */
    Optional<LeadAttachment> findById(Long id);

    /**
     * Xóa tệp đính kèm theo ID.
     * @param id ID cần xóa
     */
    void deleteById(Long id);

    /**
     * Lấy danh sách tệp đính kèm theo leadId.
     * @param leadId ID tiềm năng @return danh sách
     */
    List<LeadAttachment> findAllByLeadId(Long leadId);
}
