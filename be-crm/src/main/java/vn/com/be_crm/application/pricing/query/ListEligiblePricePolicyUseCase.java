package vn.com.be_crm.application.pricing.query;

import vn.com.be_crm.application.pricing.dto.PricePolicyResult;
import vn.com.be_crm.application.pricing.mapper.PricePolicyCommandMapper;
import vn.com.be_crm.core.page.PageRequest;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyCustomerRepository;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// lấy danh sách chính sách giá mà khách hàng đang chọn (nếu có) được phép sử dụng — dùng cho
// dropdown "Chính sách giá" ở form Cơ hội/Báo giá. Không còn lọc theo nhân viên (bảng
// price_policy_employees đã bỏ) — mọi user hợp lệ theo vai trò đều thấy đủ danh sách.
public class ListEligiblePricePolicyUseCase {
    private final IPricePolicyRepository policyRepo;
    private final IPricePolicyCustomerRepository customerRepo;

    public ListEligiblePricePolicyUseCase(IPricePolicyRepository policyRepo, IPricePolicyCustomerRepository customerRepo) {
        this.policyRepo = policyRepo; this.customerRepo = customerRepo;
    }

    // customerId: ID khách hàng đang chọn (null = không lọc theo khách hàng)
    public List<PricePolicyResult> execute(Long customerId) {
        var all = policyRepo.findAll(PageRequest.builder().page(0).size(1000).build()).getItems();

        Set<Long> byCustomer = customerId != null ? new HashSet<>(customerRepo.findEligiblePolicyIdsForCustomer(customerId)) : null;

        return all.stream()
                .filter(p -> byCustomer == null || byCustomer.contains(p.getId()))
                .map(PricePolicyCommandMapper::toResult)
                .collect(Collectors.toList());
    }
}
