package vn.com.be_crm.application.pricing.command;

import vn.com.be_crm.application.pricing.dto.PricePolicyProductResult;
import vn.com.be_crm.application.pricing.dto.UpdatePricePolicyProductCommand;
import vn.com.be_crm.application.pricing.mapper.PricePolicyProductCommandMapper;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.pricing.entity.PricePolicyProduct;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyProductRepository;
import vn.com.be_crm.core.error.frontend.NotFoundException;

/** Use case cập nhật sản phẩm trong chính sách giá. */
public class UpdatePricePolicyProductUseCase implements IUseCase<UpdatePricePolicyProductCommand, PricePolicyProductResult> {
    private final IPricePolicyProductRepository repo;
    /** @param repo port lưu trữ */
    public UpdatePricePolicyProductUseCase(IPricePolicyProductRepository repo) { this.repo = repo; }
    /** Cập nhật PricePolicyProduct. @param cmd @return PricePolicyProductResult @throws NotFoundException */
    @Override public PricePolicyProductResult execute(UpdatePricePolicyProductCommand cmd) {
        PricePolicyProduct e = repo.findById(cmd.getId()).orElseThrow(() -> new NotFoundException("PricePolicyProduct not found: " + cmd.getId()));
        return PricePolicyProductCommandMapper.toResult(repo.save(PricePolicyProductCommandMapper.toEntity(cmd, e)));
    }
}
