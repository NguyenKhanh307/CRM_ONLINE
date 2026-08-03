package vn.com.be_crm.application.pricing.query;

import vn.com.be_crm.application.pricing.dto.PricePolicyCustomerResult;
import vn.com.be_crm.application.pricing.mapper.PricePolicySubEntityCommandMapper;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyCustomerRepository;

import java.util.List;
import java.util.stream.Collectors;

/** Use case lấy danh sách khách hàng theo pricePolicyId. */
public class ListPricePolicyCustomerUseCase implements IUseCase<Long, List<PricePolicyCustomerResult>> {
    private final IPricePolicyCustomerRepository repo;
    /** @param repo port lưu trữ */
    public ListPricePolicyCustomerUseCase(IPricePolicyCustomerRepository repo) { this.repo = repo; }
    /** Lấy danh sách PricePolicyCustomer. @param id @return danh sách */
    @Override public List<PricePolicyCustomerResult> execute(Long id) {
        return repo.findAllByPricePolicyId(id).stream().map(PricePolicySubEntityCommandMapper::toCustomerResult).collect(Collectors.toList());
    }
}
