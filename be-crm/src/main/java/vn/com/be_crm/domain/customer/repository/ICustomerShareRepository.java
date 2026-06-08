package vn.com.be_crm.domain.customer.repository;

import vn.com.be_crm.domain.customer.entity.CustomerShare;

import java.util.List;

/**
 * Port lưu trữ cho CustomerShare (N:N).
 */
public interface ICustomerShareRepository {

    /**
     * Lưu bản ghi chia sẻ.
     * @param share domain entity @return entity sau khi lưu
     */
    CustomerShare save(CustomerShare share);

    /**
     * Xóa bản ghi chia sẻ theo customerId và userId.
     * @param customerId ID khách hàng @param userId ID người dùng
     */
    void deleteByCustomerIdAndUserId(Long customerId, Long userId);

    /**
     * Lấy danh sách chia sẻ theo customerId.
     * @param customerId ID khách hàng @return danh sách CustomerShare
     */
    List<CustomerShare> findByCustomerId(Long customerId);
}
