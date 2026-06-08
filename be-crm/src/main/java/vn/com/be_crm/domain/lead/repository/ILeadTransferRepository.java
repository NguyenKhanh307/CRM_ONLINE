package vn.com.be_crm.domain.lead.repository;

import vn.com.be_crm.domain.lead.entity.LeadTransfer;

import java.util.List;
import java.util.Optional;

/**
 * Port lưu trữ cho LeadTransfer.
 */
public interface ILeadTransferRepository {

    /**
     * Lưu mới hoặc cập nhật bản ghi chuyển giao.
     * @param transfer domain entity @return entity sau khi lưu
     */
    LeadTransfer save(LeadTransfer transfer);

    /**
     * Tìm bản ghi chuyển giao theo ID.
     * @param id ID @return Optional
     */
    Optional<LeadTransfer> findById(Long id);

    /**
     * Xóa bản ghi chuyển giao theo ID.
     * @param id ID cần xóa
     */
    void deleteById(Long id);

    /**
     * Lấy danh sách chuyển giao theo leadId.
     * @param leadId ID tiềm năng @return danh sách
     */
    List<LeadTransfer> findAllByLeadId(Long leadId);
}
