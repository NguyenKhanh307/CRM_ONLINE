package vn.com.be_crm.domain.pricing.repository;

import vn.com.be_crm.domain.pricing.entity.PricePolicyProductType;

import java.util.List;
import java.util.Optional;

/**
 * Port lưu trữ cho PricePolicyProductType.
 */
public interface IPricePolicyProductTypeRepository {
    /** Lưu. @param p @return entity sau khi lưu */
    PricePolicyProductType save(PricePolicyProductType p);
    /** Tìm theo ID. @param id @return Optional */
    Optional<PricePolicyProductType> findById(Long id);
    /** Xóa. @param id */
    void deleteById(Long id);
    /** Lấy danh sách theo pricePolicyId. @param pricePolicyId @return danh sách */
    List<PricePolicyProductType> findAllByPricePolicyId(Long pricePolicyId);
}
