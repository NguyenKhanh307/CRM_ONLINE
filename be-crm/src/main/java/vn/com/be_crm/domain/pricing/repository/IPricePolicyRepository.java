package vn.com.be_crm.domain.pricing.repository;

import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.domain.pricing.entity.PricePolicy;

import java.util.Optional;

/**
 * Port lưu trữ cho PricePolicy.
 */
public interface IPricePolicyRepository {
    /** Lưu mới hoặc cập nhật chính sách giá. @param p @return entity sau khi lưu */
    PricePolicy save(PricePolicy p);
    /** Tìm chính sách giá theo ID. @param id @return Optional */
    Optional<PricePolicy> findById(Long id);
    /** Xóa chính sách giá. @param id */
    void deleteById(Long id);
    /** Lấy danh sách chính sách giá có phân trang. @param r @return PageResult */
    PageResult<PricePolicy> findAll(PageRequest r);
}
