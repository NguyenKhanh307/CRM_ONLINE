package vn.com.be_crm.application.customer.command;

import vn.com.be_crm.application.customer.dto.AssignCustomerShareCommand;
import vn.com.be_crm.application.customer.dto.CustomerShareResult;
import vn.com.be_crm.application.customer.mapper.CustomerShareCommandMapper;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.customer.repository.ICustomerShareRepository;

/** Use case gán chia sẻ khách hàng với người dùng. */
public class AssignCustomerShareUseCase implements IUseCase<AssignCustomerShareCommand, CustomerShareResult> {
    private final ICustomerShareRepository repo;
    /** @param repo port lưu trữ */
    public AssignCustomerShareUseCase(ICustomerShareRepository repo) { this.repo = repo; }

    /**
     * Gán chia sẻ khách hàng và trả về result.
     * @param cmd dữ liệu gán @return CustomerShareResult
     */
    @Override
    public CustomerShareResult execute(AssignCustomerShareCommand cmd) {
        return CustomerShareCommandMapper.toResult(repo.save(CustomerShareCommandMapper.toEntity(cmd)));
    }
}
