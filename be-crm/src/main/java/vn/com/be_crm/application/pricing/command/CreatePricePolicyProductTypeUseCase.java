package vn.com.be_crm.application.pricing.command;

import vn.com.be_crm.application.pricing.dto.CreatePricePolicyProductTypeCommand;
import vn.com.be_crm.application.pricing.dto.PricePolicyProductTypeResult;
import vn.com.be_crm.application.pricing.mapper.PricePolicySubEntityCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyProductTypeRepository;

/** Use case tạo mới loại sản phẩm trong chính sách giá. */
public class CreatePricePolicyProductTypeUseCase implements IUseCase<CreatePricePolicyProductTypeCommand, PricePolicyProductTypeResult> {
    private final IPricePolicyProductTypeRepository repo;
    /** @param repo port lưu trữ */
    public CreatePricePolicyProductTypeUseCase(IPricePolicyProductTypeRepository repo) { this.repo = repo; }
    /** Tạo mới PricePolicyProductType. @param cmd @return result */
    @Override public PricePolicyProductTypeResult execute(CreatePricePolicyProductTypeCommand cmd) {
        return PricePolicySubEntityCommandMapper.toProductTypeResult(repo.save(PricePolicySubEntityCommandMapper.toProductTypeEntity(cmd)));
    }
}
