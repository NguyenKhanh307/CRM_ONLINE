package vn.com.be_crm.application.pricing.query;

import vn.com.be_crm.application.pricing.dto.PricePolicyResult;
import vn.com.be_crm.application.pricing.mapper.PricePolicyCommandMapper;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyRepository;

import java.util.stream.Collectors;

/** Use case lấy danh sách chính sách giá có phân trang. */
public class ListPricePolicyUseCase implements IUseCase<PageRequest, PageResult<PricePolicyResult>> {
    private final IPricePolicyRepository repo;
    /** @param repo port lưu trữ */
    public ListPricePolicyUseCase(IPricePolicyRepository repo) { this.repo = repo; }
    /** Lấy danh sách PricePolicy. @param r phân trang @return PageResult */
    @Override public PageResult<PricePolicyResult> execute(PageRequest r) {
        var page = repo.findAll(r);
        return PageResult.<PricePolicyResult>builder()
                .items(page.getItems().stream().map(PricePolicyCommandMapper::toResult).collect(Collectors.toList()))
                .total(page.getTotal()).page(page.getPage()).size(page.getSize()).build();
    }
}
