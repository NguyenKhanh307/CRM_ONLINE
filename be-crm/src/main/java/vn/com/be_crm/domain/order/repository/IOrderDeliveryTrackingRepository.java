package vn.com.be_crm.domain.order.repository;

import vn.com.be_crm.domain.order.entity.OrderDeliveryTracking;

import java.util.List;
import java.util.Optional;

/**
 * Port lưu trữ cho OrderDeliveryTracking.
 */
public interface IOrderDeliveryTrackingRepository {
    /** Lưu mới hoặc cập nhật theo dõi giao hàng. @param t @return entity sau khi lưu */
    OrderDeliveryTracking save(OrderDeliveryTracking t);
    /** Tìm theo dõi giao hàng theo ID. @param id @return Optional */
    Optional<OrderDeliveryTracking> findById(Long id);
    /** Xóa theo dõi giao hàng. @param id */
    void deleteById(Long id);
    /** Lấy danh sách theo dõi giao hàng theo orderId. @param orderId @return danh sách */
    List<OrderDeliveryTracking> findAllByOrderId(Long orderId);
}
