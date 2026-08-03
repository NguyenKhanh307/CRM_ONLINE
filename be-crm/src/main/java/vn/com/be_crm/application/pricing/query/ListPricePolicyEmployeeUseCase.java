package vn.com.be_crm.application.pricing.query;

import vn.com.be_crm.application.pricing.dto.PricePolicyEmployeeResult;
import vn.com.be_crm.application.pricing.mapper.PricePolicySubEntityCommandMapper;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyEmployeeRepository;

import java.util.List;
import java.util.stream.Collectors;

/** Use case lấy danh sách nhân viên theo pricePolicyId. */
public class ListPricePolicyEmployeeUseCase implements IUseCase<Long, List<PricePolicyEmployeeResult>> {
    private final IPricePolicyEmployeeRepository repo;
    /** @param repo port lưu trữ */
    public ListPricePolicyEmployeeUseCase(IPricePolicyEmployeeRepository repo) { this.repo = repo; }
    /** Lấy danh sách PricePolicyEmployee. @param id @return danh sách */
    @Override public List<PricePolicyEmployeeResult> execute(Long id) {
        return repo.findAllByPricePolicyId(id).stream().map(PricePolicySubEntityCommandMapper::toEmployeeResult).collect(Collectors.toList());
    }
}
