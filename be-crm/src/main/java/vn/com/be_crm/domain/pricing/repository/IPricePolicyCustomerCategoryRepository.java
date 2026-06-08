package vn.com.be_crm.domain.pricing.repository;

import vn.com.be_crm.domain.pricing.entity.PricePolicyCustomerCategory;

import java.util.List;
import java.util.Optional;

/**
 * Port lưu trữ cho PricePolicyCustomerCategory.
 */
public interface IPricePolicyCustomerCategoryRepository {
    /** Lưu. @param p @return entity sau khi lưu */
    PricePolicyCustomerCategory save(PricePolicyCustomerCategory p);
    /** Tìm theo ID. @param id @return Optional */
    Optional<PricePolicyCustomerCategory> findById(Long id);
    /** Xóa. @param id */
    void deleteById(Long id);
    /** Lấy danh sách theo pricePolicyId. @param pricePolicyId @return danh sách */
    List<PricePolicyCustomerCategory> findAllByPricePolicyId(Long pricePolicyId);
}
