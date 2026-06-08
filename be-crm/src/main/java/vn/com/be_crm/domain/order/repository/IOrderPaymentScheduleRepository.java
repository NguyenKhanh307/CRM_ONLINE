package vn.com.be_crm.domain.order.repository;

import vn.com.be_crm.domain.order.entity.OrderPaymentSchedule;

import java.util.List;
import java.util.Optional;

/**
 * Port lưu trữ cho OrderPaymentSchedule.
 */
public interface IOrderPaymentScheduleRepository {
    /** Lưu mới hoặc cập nhật đợt thanh toán. @param s @return entity sau khi lưu */
    OrderPaymentSchedule save(OrderPaymentSchedule s);
    /** Tìm đợt thanh toán theo ID. @param id @return Optional */
    Optional<OrderPaymentSchedule> findById(Long id);
    /** Xóa đợt thanh toán. @param id */
    void deleteById(Long id);
    /** Lấy danh sách đợt thanh toán theo orderId. @param orderId @return danh sách */
    List<OrderPaymentSchedule> findAllByOrderId(Long orderId);
}
