package vn.com.be_crm.application.pricing.command;

import vn.com.be_crm.application.pricing.dto.PricePolicyResult;
import vn.com.be_crm.application.pricing.dto.UpdatePricePolicyCommand;
import vn.com.be_crm.application.pricing.mapper.PricePolicyCommandMapper;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.pricing.entity.PricePolicy;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyRepository;
import vn.com.be_crm.core.error.frontend.NotFoundException;

/** Use case cập nhật chính sách giá. */
public class UpdatePricePolicyUseCase implements IUseCase<UpdatePricePolicyCommand, PricePolicyResult> {
    private final IPricePolicyRepository repo;
    /** @param repo port lưu trữ */
    public UpdatePricePolicyUseCase(IPricePolicyRepository repo) { this.repo = repo; }
    /** Cập nhật PricePolicy. @param cmd @return PricePolicyResult @throws NotFoundException */
    @Override public PricePolicyResult execute(UpdatePricePolicyCommand cmd) {
        PricePolicy e = repo.findById(cmd.getId()).orElseThrow(() -> new NotFoundException("PricePolicy not found: " + cmd.getId()));
        return PricePolicyCommandMapper.toResult(repo.save(PricePolicyCommandMapper.toEntity(cmd, e)));
    }
}
