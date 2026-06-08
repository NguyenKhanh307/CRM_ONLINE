package vn.com.be_crm.domain.customer.repository;

import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.domain.customer.entity.Customer;

import java.util.Optional;

/**
 * Port lưu trữ cho Customer.
 */
public interface ICustomerRepository {

    /**
     * Lưu mới hoặc cập nhật khách hàng.
     * @param customer domain entity @return entity sau khi lưu
     */
    Customer save(Customer customer);

    /**
     * Tìm khách hàng theo ID (chưa xóa mềm).
     * @param id ID @return Optional
     */
    Optional<Customer> findById(Long id);

    /**
     * Xóa mềm khách hàng theo ID.
     * @param id ID cần xóa
     */
    void deleteById(Long id);

    /**
     * Lấy danh sách khách hàng chưa xóa có phân trang.
     * @param r tham số phân trang @return PageResult
     */
    PageResult<Customer> findAll(PageRequest r);
}
