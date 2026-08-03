package vn.com.be_crm.application.pricing.query;

import vn.com.be_crm.application.pricing.dto.PricePolicyResult;
import vn.com.be_crm.application.pricing.mapper.PricePolicyCommandMapper;
import vn.com.be_crm.core.page.PageRequest;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyCustomerRepository;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyEmployeeRepository;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Use case lấy danh sách chính sách giá mà người gọi (và khách hàng đang chọn, nếu có) được phép sử
 * dụng — dùng cho dropdown "Chính sách giá" ở form Cơ hội/Báo giá. Khác với {@link ListPricePolicyUseCase}
 * (danh sách đầy đủ cho trang quản trị), endpoint này lọc theo price_policy_employees/customers.
 */
public class ListEligiblePricePolicyUseCase {
    private final IPricePolicyRepository policyRepo;
    private final IPricePolicyEmployeeRepository employeeRepo;
    private final IPricePolicyCustomerRepository customerRepo;

    /** @param policyRepo port chính sách giá @param employeeRepo port danh sách nhân viên áp dụng @param customerRepo port danh sách khách hàng áp dụng */
    public ListEligiblePricePolicyUseCase(IPricePolicyRepository policyRepo, IPricePolicyEmployeeRepository employeeRepo,
                                           IPricePolicyCustomerRepository customerRepo) {
        this.policyRepo = policyRepo; this.employeeRepo = employeeRepo; this.customerRepo = customerRepo;
    }

    /**
     * Lấy chính sách giá hợp lệ cho người gọi/khách hàng.
     * @param userId ID người gọi @param privileged true nếu ADMIN/SALES_MANAGER (bỏ qua lọc theo nhân viên)
     * @param customerId ID khách hàng đang chọn (null = không lọc theo khách hàng)
     * @return danh sách chính sách giá hợp lệ
     */
    public List<PricePolicyResult> execute(Long userId, boolean privileged, Long customerId) {
        var all = policyRepo.findAll(PageRequest.builder().page(0).size(1000).build()).getItems();

        Set<Long> byEmployee = privileged ? null : new HashSet<>(employeeRepo.findEligiblePolicyIdsForUser(userId));
        Set<Long> byCustomer = customerId != null ? new HashSet<>(customerRepo.findEligiblePolicyIdsForCustomer(customerId)) : null;

        return all.stream()
                .filter(p -> byEmployee == null || byEmployee.contains(p.getId()))
                .filter(p -> byCustomer == null || byCustomer.contains(p.getId()))
                .map(PricePolicyCommandMapper::toResult)
                .collect(Collectors.toList());
    }
}
