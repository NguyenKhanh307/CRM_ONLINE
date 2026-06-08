package vn.com.be_crm.application.customer.query;

import vn.com.be_crm.application.customer.dto.CustomerResult;
import vn.com.be_crm.application.customer.mapper.CustomerCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.customer.repository.ICustomerRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case lấy khách hàng theo ID. */
public class GetCustomerUseCase implements IUseCase<Long, CustomerResult> {
    private final ICustomerRepository repo;
    /** @param repo port lưu trữ */
    public GetCustomerUseCase(ICustomerRepository repo) { this.repo = repo; }

    /**
     * Lấy Customer theo ID.
     * @param id ID @return CustomerResult
     * @throws NotFoundException nếu không tìm thấy
     */
    @Override
    public CustomerResult execute(Long id) {
        return CustomerCommandMapper.toResult(
                repo.findById(id).orElseThrow(() -> new NotFoundException("Customer not found: " + id)));
    }
}
