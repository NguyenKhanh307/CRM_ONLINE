package vn.com.be_crm.domain.pricing.repository;

import vn.com.be_crm.domain.pricing.entity.PricePolicyProductCategory;

import java.util.List;
import java.util.Optional;

/**
 * Port lưu trữ cho PricePolicyProductCategory.
 */
public interface IPricePolicyProductCategoryRepository {
    /** Lưu. @param p @return entity sau khi lưu */
    PricePolicyProductCategory save(PricePolicyProductCategory p);
    /** Tìm theo ID. @param id @return Optional */
    Optional<PricePolicyProductCategory> findById(Long id);
    /** Xóa. @param id */
    void deleteById(Long id);
    /** Lấy danh sách theo pricePolicyId. @param pricePolicyId @return danh sách */
    List<PricePolicyProductCategory> findAllByPricePolicyId(Long pricePolicyId);
}
