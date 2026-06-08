package vn.com.be_crm.domain.pricing.repository;

import vn.com.be_crm.domain.pricing.entity.PricePolicyProduct;

import java.util.List;
import java.util.Optional;

/**
 * Port lưu trữ cho PricePolicyProduct.
 */
public interface IPricePolicyProductRepository {
    /** Lưu. @param p @return entity sau khi lưu */
    PricePolicyProduct save(PricePolicyProduct p);
    /** Tìm theo ID. @param id @return Optional */
    Optional<PricePolicyProduct> findById(Long id);
    /** Xóa. @param id */
    void deleteById(Long id);
    /** Lấy danh sách theo pricePolicyId. @param pricePolicyId @return danh sách */
    List<PricePolicyProduct> findAllByPricePolicyId(Long pricePolicyId);
}
