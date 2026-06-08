package vn.com.be_crm.domain.pricing.repository;

import vn.com.be_crm.domain.pricing.entity.PricePolicyCustomer;

import java.util.List;
import java.util.Optional;

/**
 * Port lưu trữ cho PricePolicyCustomer.
 */
public interface IPricePolicyCustomerRepository {
    /** Lưu. @param p @return entity sau khi lưu */
    PricePolicyCustomer save(PricePolicyCustomer p);
    /** Tìm theo ID. @param id @return Optional */
    Optional<PricePolicyCustomer> findById(Long id);
    /** Xóa. @param id */
    void deleteById(Long id);
    /** Lấy danh sách theo pricePolicyId. @param pricePolicyId @return danh sách */
    List<PricePolicyCustomer> findAllByPricePolicyId(Long pricePolicyId);
}
