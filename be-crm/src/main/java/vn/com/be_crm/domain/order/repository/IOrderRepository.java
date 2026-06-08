package vn.com.be_crm.domain.order.repository;

import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.domain.order.entity.Order;

import java.util.Optional;

/**
 * Port lưu trữ cho Order.
 */
public interface IOrderRepository {
    /** Lưu mới hoặc cập nhật đơn hàng. @param o @return entity sau khi lưu */
    Order save(Order o);
    /** Tìm đơn hàng theo ID. @param id @return Optional */
    Optional<Order> findById(Long id);
    /** Xóa mềm đơn hàng. @param id */
    void deleteById(Long id);
    /** Lấy danh sách đơn hàng có phân trang. @param r @return PageResult */
    PageResult<Order> findAll(PageRequest r);
}
