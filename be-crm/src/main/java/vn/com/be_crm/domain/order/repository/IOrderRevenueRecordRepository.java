package vn.com.be_crm.domain.order.repository;

import vn.com.be_crm.domain.order.entity.OrderRevenueRecord;

import java.util.List;
import java.util.Optional;

/**
 * Port lưu trữ cho OrderRevenueRecord.
 */
public interface IOrderRevenueRecordRepository {
    /** Lưu bản ghi doanh thu. @param r @return entity sau khi lưu */
    OrderRevenueRecord save(OrderRevenueRecord r);
    /** Tìm bản ghi doanh thu theo ID. @param id @return Optional */
    Optional<OrderRevenueRecord> findById(Long id);
    /** Xóa bản ghi doanh thu. @param id */
    void deleteById(Long id);
    /** Lấy danh sách bản ghi doanh thu theo orderId. @param orderId @return danh sách */
    List<OrderRevenueRecord> findAllByOrderId(Long orderId);
}
