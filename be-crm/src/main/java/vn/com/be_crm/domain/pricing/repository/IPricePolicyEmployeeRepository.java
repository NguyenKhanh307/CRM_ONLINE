package vn.com.be_crm.domain.pricing.repository;

import vn.com.be_crm.domain.pricing.entity.PricePolicyEmployee;

import java.util.List;
import java.util.Optional;

/**
 * Port lưu trữ cho PricePolicyEmployee.
 */
public interface IPricePolicyEmployeeRepository {
    /** Lưu. @param p @return entity sau khi lưu */
    PricePolicyEmployee save(PricePolicyEmployee p);
    /** Tìm theo ID. @param id @return Optional */
    Optional<PricePolicyEmployee> findById(Long id);
    /** Xóa. @param id */
    void deleteById(Long id);
    /** Lấy danh sách theo pricePolicyId. @param pricePolicyId @return danh sách */
    List<PricePolicyEmployee> findAllByPricePolicyId(Long pricePolicyId);
    /** ID các chính sách userId được phép dùng: không giới hạn nhân viên (0 dòng) HOẶC có dòng khớp userId. @param userId @return danh sách policy ID */
    List<Long> findEligiblePolicyIdsForUser(Long userId);
    /** true nếu policy không giới hạn nhân viên (0 dòng) HOẶC có dòng khớp userId. @param pricePolicyId @param userId @return có hợp lệ không */
    boolean isEligibleForUser(Long pricePolicyId, Long userId);
}
