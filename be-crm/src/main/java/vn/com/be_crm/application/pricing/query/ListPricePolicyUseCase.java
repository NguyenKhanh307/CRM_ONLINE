package vn.com.be_crm.application.pricing.query;

import vn.com.be_crm.application.pricing.dto.PricePolicyResult;
import vn.com.be_crm.application.pricing.mapper.PricePolicyCommandMapper;
import vn.com.be_crm.core.page.PageRequest;
import vn.com.be_crm.core.page.PageResult;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyRepository;

import java.util.stream.Collectors;

/** Use case lấy danh sách chính sách giá có phân trang. */
public class ListPricePolicyUseCase implements IUseCase<PageRequest, PageResult<PricePolicyResult>> {
    private final IPricePolicyRepository repo;
    private final PricePolicyExpiryUseCase expiryUC;
    /** @param repo port lưu trữ @param expiryUC tự đổi trạng thái khi phát hiện quá hạn hiệu lực */
    public ListPricePolicyUseCase(IPricePolicyRepository repo, PricePolicyExpiryUseCase expiryUC) {
        this.repo = repo; this.expiryUC = expiryUC;
    }
    /** Lấy danh sách PricePolicy. @param r phân trang @return PageResult */
    @Override public PageResult<PricePolicyResult> execute(PageRequest r) {
        var page = repo.findAll(r);
        return PageResult.<PricePolicyResult>builder()
                .items(page.getItems().stream().map(expiryUC::checkAndExpire)
                        .map(PricePolicyCommandMapper::toResult).collect(Collectors.toList()))
                .total(page.getTotal()).page(page.getPage()).size(page.getSize()).build();
    }
}
