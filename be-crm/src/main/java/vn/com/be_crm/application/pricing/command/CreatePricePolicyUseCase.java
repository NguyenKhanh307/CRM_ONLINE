package vn.com.be_crm.application.pricing.command;

import vn.com.be_crm.application.pricing.dto.CreatePricePolicyCommand;
import vn.com.be_crm.application.pricing.dto.PricePolicyResult;
import vn.com.be_crm.application.pricing.mapper.PricePolicyCommandMapper;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyRepository;

/** Use case tạo mới chính sách giá. */
public class CreatePricePolicyUseCase implements IUseCase<CreatePricePolicyCommand, PricePolicyResult> {
    private final IPricePolicyRepository repo;
    /** @param repo port lưu trữ */
    public CreatePricePolicyUseCase(IPricePolicyRepository repo) { this.repo = repo; }
    /** Tạo mới PricePolicy. @param cmd @return PricePolicyResult */
    @Override public PricePolicyResult execute(CreatePricePolicyCommand cmd) {
        return PricePolicyCommandMapper.toResult(repo.save(PricePolicyCommandMapper.toEntity(cmd)));
    }
}
