package vn.com.be_crm.application.pricing.query;

import vn.com.be_crm.application.pricing.dto.PricePolicyProductTypeResult;
import vn.com.be_crm.application.pricing.mapper.PricePolicySubEntityCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyProductTypeRepository;

import java.util.List;
import java.util.stream.Collectors;

/** Use case lấy danh sách loại sản phẩm theo pricePolicyId. */
public class ListPricePolicyProductTypeUseCase implements IUseCase<Long, List<PricePolicyProductTypeResult>> {
    private final IPricePolicyProductTypeRepository repo;
    /** @param repo port lưu trữ */
    public ListPricePolicyProductTypeUseCase(IPricePolicyProductTypeRepository repo) { this.repo = repo; }
    /** Lấy danh sách PricePolicyProductType. @param id @return danh sách */
    @Override public List<PricePolicyProductTypeResult> execute(Long id) {
        return repo.findAllByPricePolicyId(id).stream().map(PricePolicySubEntityCommandMapper::toProductTypeResult).collect(Collectors.toList());
    }
}
