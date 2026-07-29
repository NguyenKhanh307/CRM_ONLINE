package vn.com.be_crm.application.pricing.query;

import vn.com.be_crm.application.pricing.dto.PricePolicyProductCategoryResult;
import vn.com.be_crm.application.pricing.mapper.PricePolicySubEntityCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyProductCategoryRepository;

import java.util.List;
import java.util.stream.Collectors;

/** Use case lấy danh sách danh mục sản phẩm theo pricePolicyId. */
public class ListPricePolicyProductCategoryUseCase implements IUseCase<Long, List<PricePolicyProductCategoryResult>> {
    private final IPricePolicyProductCategoryRepository repo;
    /** @param repo port lưu trữ */
    public ListPricePolicyProductCategoryUseCase(IPricePolicyProductCategoryRepository repo) { this.repo = repo; }
    /** Lấy danh sách PricePolicyProductCategory. @param id @return danh sách */
    @Override public List<PricePolicyProductCategoryResult> execute(Long id) {
        return repo.findAllByPricePolicyId(id).stream().map(PricePolicySubEntityCommandMapper::toCategoryResult).collect(Collectors.toList());
    }
}
