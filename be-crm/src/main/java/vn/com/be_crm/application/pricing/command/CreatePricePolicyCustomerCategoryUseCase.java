package vn.com.be_crm.application.pricing.command;

import vn.com.be_crm.application.pricing.dto.CreatePricePolicyCustomerCategoryCommand;
import vn.com.be_crm.application.pricing.dto.PricePolicyCustomerCategoryResult;
import vn.com.be_crm.application.pricing.mapper.PricePolicySubEntityCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyCustomerCategoryRepository;

/** Use case tạo mới danh mục khách hàng trong chính sách giá. */
public class CreatePricePolicyCustomerCategoryUseCase implements IUseCase<CreatePricePolicyCustomerCategoryCommand, PricePolicyCustomerCategoryResult> {
    private final IPricePolicyCustomerCategoryRepository repo;
    /** @param repo port lưu trữ */
    public CreatePricePolicyCustomerCategoryUseCase(IPricePolicyCustomerCategoryRepository repo) { this.repo = repo; }
    /** Tạo mới PricePolicyCustomerCategory. @param cmd @return result */
    @Override public PricePolicyCustomerCategoryResult execute(CreatePricePolicyCustomerCategoryCommand cmd) {
        return PricePolicySubEntityCommandMapper.toCategoryResult(repo.save(PricePolicySubEntityCommandMapper.toCategoryEntity(cmd)));
    }
}
