package vn.com.be_crm.domain.order.repository;

import vn.com.be_crm.domain.order.entity.OrderItem;

import java.util.List;
import java.util.Optional;

/**
 * Port lưu trữ cho OrderItem.
 */
public interface IOrderItemRepository {
    /** Lưu mới hoặc cập nhật dòng đơn hàng. @param i @return entity sau khi lưu */
    OrderItem save(OrderItem i);
    /** Tìm dòng đơn hàng theo ID. @param id @return Optional */
    Optional<OrderItem> findById(Long id);
    /** Xóa dòng đơn hàng. @param id */
    void deleteById(Long id);
    /** Lấy danh sách dòng đơn hàng theo orderId. @param orderId @return danh sách */
    List<OrderItem> findAllByOrderId(Long orderId);
}
