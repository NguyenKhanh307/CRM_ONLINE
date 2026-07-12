package vn.com.be_crm.application.customer.command;

import vn.com.be_crm.application.customer.dto.CreateCustomerCommand;
import vn.com.be_crm.application.customer.dto.CustomerResult;
import vn.com.be_crm.application.customer.mapper.CustomerCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.shared.exception.DomainException;
import vn.com.be_crm.domain.customer.repository.ICustomerRepository;

/** Use case tạo mới khách hàng. */
public class CreateCustomerUseCase implements IUseCase<CreateCustomerCommand, CustomerResult> {
    private final ICustomerRepository repo;
    /** @param repo port lưu trữ */
    public CreateCustomerUseCase(ICustomerRepository repo) { this.repo = repo; }

    /**
     * Tạo mới Customer và trả về result.
     * @param cmd dữ liệu tạo mới @return CustomerResult
     */
    @Override
    public CustomerResult execute(CreateCustomerCommand cmd) {
        // Check trùng thân thiện trước khi insert (DB unique constraint là lớp phòng thủ 2)
        if (cmd.getCode() != null && repo.findByCode(cmd.getCode()).isPresent()) {
            throw new DomainException("Mã khách hàng \"" + cmd.getCode() + "\" đã tồn tại, vui lòng dùng mã khác");
        }
        return CustomerCommandMapper.toResult(repo.save(CustomerCommandMapper.toEntity(cmd)));
    }
}
