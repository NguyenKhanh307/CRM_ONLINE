package vn.com.be_crm.application.pricing.command;

import vn.com.be_crm.application.pricing.dto.CreatePricePolicyEmployeeCommand;
import vn.com.be_crm.application.pricing.dto.PricePolicyEmployeeResult;
import vn.com.be_crm.application.pricing.mapper.PricePolicySubEntityCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyEmployeeRepository;

/** Use case tạo mới nhân viên trong chính sách giá. */
public class CreatePricePolicyEmployeeUseCase implements IUseCase<CreatePricePolicyEmployeeCommand, PricePolicyEmployeeResult> {
    private final IPricePolicyEmployeeRepository repo;
    /** @param repo port lưu trữ */
    public CreatePricePolicyEmployeeUseCase(IPricePolicyEmployeeRepository repo) { this.repo = repo; }
    /** Tạo mới PricePolicyEmployee. @param cmd @return result */
    @Override public PricePolicyEmployeeResult execute(CreatePricePolicyEmployeeCommand cmd) {
        return PricePolicySubEntityCommandMapper.toEmployeeResult(repo.save(PricePolicySubEntityCommandMapper.toEmployeeEntity(cmd)));
    }
}
