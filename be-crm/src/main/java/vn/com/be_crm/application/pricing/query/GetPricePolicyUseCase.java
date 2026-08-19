package vn.com.be_crm.application.pricing.query;

import vn.com.be_crm.application.pricing.dto.PricePolicyResult;
import vn.com.be_crm.application.pricing.mapper.PricePolicyCommandMapper;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyRepository;
import vn.com.be_crm.core.error.frontend.NotFoundException;

/** Use case lấy chính sách giá theo ID. */
public class GetPricePolicyUseCase implements IUseCase<Long, PricePolicyResult> {
    private final IPricePolicyRepository repo;
    private final PricePolicyExpiryUseCase expiryUC;
    /** @param repo port lưu trữ @param expiryUC tự đổi trạng thái khi phát hiện quá hạn hiệu lực */
    public GetPricePolicyUseCase(IPricePolicyRepository repo, PricePolicyExpiryUseCase expiryUC) {
        this.repo = repo; this.expiryUC = expiryUC;
    }
    /** Lấy PricePolicy theo ID. @param id @return PricePolicyResult @throws NotFoundException */
    @Override public PricePolicyResult execute(Long id) {
        var p = repo.findById(id).orElseThrow(() -> new NotFoundException("PricePolicy not found: " + id));
        return PricePolicyCommandMapper.toResult(expiryUC.checkAndExpire(p));
    }
}
