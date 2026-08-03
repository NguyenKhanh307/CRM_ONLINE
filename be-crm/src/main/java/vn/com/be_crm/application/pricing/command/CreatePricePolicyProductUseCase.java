package vn.com.be_crm.application.pricing.command;

import vn.com.be_crm.application.pricing.dto.CreatePricePolicyProductCommand;
import vn.com.be_crm.application.pricing.dto.PricePolicyProductResult;
import vn.com.be_crm.application.pricing.mapper.PricePolicyProductCommandMapper;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyProductRepository;

/** Use case tạo mới sản phẩm trong chính sách giá. */
public class CreatePricePolicyProductUseCase implements IUseCase<CreatePricePolicyProductCommand, PricePolicyProductResult> {
    private final IPricePolicyProductRepository repo;
    /** @param repo port lưu trữ */
    public CreatePricePolicyProductUseCase(IPricePolicyProductRepository repo) { this.repo = repo; }
    /** Tạo mới PricePolicyProduct. @param cmd @return PricePolicyProductResult */
    @Override public PricePolicyProductResult execute(CreatePricePolicyProductCommand cmd) {
        return PricePolicyProductCommandMapper.toResult(repo.save(PricePolicyProductCommandMapper.toEntity(cmd)));
    }
}
