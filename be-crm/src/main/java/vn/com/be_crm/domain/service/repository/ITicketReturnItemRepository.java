package vn.com.be_crm.domain.service.repository;

import vn.com.be_crm.domain.service.entity.TicketReturnItem;

import java.util.List;
import java.util.Optional;

/**
 * Port lưu trữ cho TicketReturnItem (dòng hàng trả/đổi).
 */
public interface ITicketReturnItemRepository {
    /** Lưu mới hoặc cập nhật dòng hàng. @param item @return entity sau khi lưu */
    TicketReturnItem save(TicketReturnItem item);
    /** Tìm dòng hàng theo ID. @param id @return Optional */
    Optional<TicketReturnItem> findById(Long id);
    /** Xóa dòng hàng theo ID. @param id */
    void deleteById(Long id);
    /** Lấy danh sách dòng hàng theo ticketId. @param ticketId @return danh sách */
    List<TicketReturnItem> findAllByTicketId(Long ticketId);
}
