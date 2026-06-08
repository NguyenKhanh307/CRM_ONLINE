package vn.com.be_crm.application.pricing.query;

import vn.com.be_crm.application.pricing.dto.PricePolicyCustomerCategoryResult;
import vn.com.be_crm.application.pricing.mapper.PricePolicySubEntityCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyCustomerCategoryRepository;

import java.util.List;
import java.util.stream.Collectors;

/** Use case lấy danh sách danh mục khách hàng theo pricePolicyId. */
public class ListPricePolicyCustomerCategoryUseCase implements IUseCase<Long, List<PricePolicyCustomerCategoryResult>> {
    private final IPricePolicyCustomerCategoryRepository repo;
    /** @param repo port lưu trữ */
    public ListPricePolicyCustomerCategoryUseCase(IPricePolicyCustomerCategoryRepository repo) { this.repo = repo; }
    /** Lấy danh sách PricePolicyCustomerCategory. @param id @return danh sách */
    @Override public List<PricePolicyCustomerCategoryResult> execute(Long id) {
        return repo.findAllByPricePolicyId(id).stream().map(PricePolicySubEntityCommandMapper::toCategoryResult).collect(Collectors.toList());
    }
}
