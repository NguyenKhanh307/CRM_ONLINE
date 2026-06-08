package vn.com.be_crm.application.customer.command;

import vn.com.be_crm.application.customer.dto.CustomerResult;
import vn.com.be_crm.application.customer.dto.UpdateCustomerCommand;
import vn.com.be_crm.application.customer.mapper.CustomerCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.customer.entity.Customer;
import vn.com.be_crm.domain.customer.repository.ICustomerRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case cập nhật khách hàng. */
public class UpdateCustomerUseCase implements IUseCase<UpdateCustomerCommand, CustomerResult> {
    private final ICustomerRepository repo;
    /** @param repo port lưu trữ */
    public UpdateCustomerUseCase(ICustomerRepository repo) { this.repo = repo; }

    /**
     * Cập nhật Customer và trả về result.
     * @param cmd dữ liệu cập nhật @return CustomerResult
     * @throws NotFoundException nếu không tìm thấy
     */
    @Override
    public CustomerResult execute(UpdateCustomerCommand cmd) {
        Customer existing = repo.findById(cmd.getId())
                .orElseThrow(() -> new NotFoundException("Customer not found: " + cmd.getId()));
        return CustomerCommandMapper.toResult(repo.save(CustomerCommandMapper.toEntity(cmd, existing)));
    }
}
