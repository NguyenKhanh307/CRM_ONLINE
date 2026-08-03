package vn.com.be_crm.application.pricing.command;

import vn.com.be_crm.application.pricing.dto.CreatePricePolicyCustomerCommand;
import vn.com.be_crm.application.pricing.dto.PricePolicyCustomerResult;
import vn.com.be_crm.application.pricing.mapper.PricePolicySubEntityCommandMapper;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyCustomerRepository;

/** Use case tạo mới khách hàng trong chính sách giá. */
public class CreatePricePolicyCustomerUseCase implements IUseCase<CreatePricePolicyCustomerCommand, PricePolicyCustomerResult> {
    private final IPricePolicyCustomerRepository repo;
    /** @param repo port lưu trữ */
    public CreatePricePolicyCustomerUseCase(IPricePolicyCustomerRepository repo) { this.repo = repo; }
    /** Tạo mới PricePolicyCustomer. @param cmd @return PricePolicyCustomerResult */
    @Override public PricePolicyCustomerResult execute(CreatePricePolicyCustomerCommand cmd) {
        return PricePolicySubEntityCommandMapper.toCustomerResult(repo.save(PricePolicySubEntityCommandMapper.toCustomerEntity(cmd)));
    }
}
