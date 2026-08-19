package vn.com.be_crm.domain.service.repository;

import vn.com.be_crm.domain.service.entity.SlaPolicy;
import vn.com.be_crm.domain.service.enums.TicketPriority;

import java.util.List;
import java.util.Optional;

/**
 * Port lưu trữ cho SlaPolicy (chính sách SLA).
 */
public interface ISlaPolicyRepository {
    /** Lấy toàn bộ chính sách SLA. @return danh sách */
    List<SlaPolicy> findAll();
    /**
     * Tìm chính sách SLA còn hiệu lực theo độ ưu tiên — dùng khi tạo phiếu để tính hạn giải quyết.
     * @param priority độ ưu tiên @return Optional
     */
    Optional<SlaPolicy> findByPriority(TicketPriority priority);
    /** Lưu (tạo mới hoặc cập nhật) chính sách SLA. @param s entity @return đã lưu */
    SlaPolicy save(SlaPolicy s);
    /** Tìm chính sách SLA theo ID. @param id ID @return Optional */
    Optional<SlaPolicy> findById(Long id);
    /** Xóa chính sách SLA theo ID. @param id ID */
    void deleteById(Long id);
}
