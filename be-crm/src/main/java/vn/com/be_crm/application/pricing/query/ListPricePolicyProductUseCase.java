package vn.com.be_crm.application.pricing.query;

import vn.com.be_crm.application.pricing.dto.PricePolicyProductResult;
import vn.com.be_crm.application.pricing.mapper.PricePolicyProductCommandMapper;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyProductRepository;

import java.util.List;
import java.util.stream.Collectors;

/** Use case lấy danh sách sản phẩm theo pricePolicyId. */
public class ListPricePolicyProductUseCase implements IUseCase<Long, List<PricePolicyProductResult>> {
    private final IPricePolicyProductRepository repo;
    /** @param repo port lưu trữ */
    public ListPricePolicyProductUseCase(IPricePolicyProductRepository repo) { this.repo = repo; }
    /** Lấy danh sách PricePolicyProduct theo pricePolicyId. @param id @return danh sách */
    @Override public List<PricePolicyProductResult> execute(Long id) {
        return repo.findAllByPricePolicyId(id).stream().map(PricePolicyProductCommandMapper::toResult).collect(Collectors.toList());
    }
}
