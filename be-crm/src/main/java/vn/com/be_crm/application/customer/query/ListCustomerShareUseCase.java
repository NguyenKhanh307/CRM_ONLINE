package vn.com.be_crm.application.customer.query;

import vn.com.be_crm.application.customer.dto.CustomerShareResult;
import vn.com.be_crm.application.customer.mapper.CustomerShareCommandMapper;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.customer.repository.ICustomerShareRepository;

import java.util.List;
import java.util.stream.Collectors;

/** Use case lấy danh sách chia sẻ theo customerId. */
public class ListCustomerShareUseCase implements IUseCase<Long, List<CustomerShareResult>> {
    private final ICustomerShareRepository repo;
    /** @param repo port lưu trữ */
    public ListCustomerShareUseCase(ICustomerShareRepository repo) { this.repo = repo; }

    /**
     * Lấy danh sách CustomerShare theo customerId.
     * @param customerId ID khách hàng @return danh sách result
     */
    @Override
    public List<CustomerShareResult> execute(Long customerId) {
        return repo.findByCustomerId(customerId).stream()
                .map(CustomerShareCommandMapper::toResult).collect(Collectors.toList());
    }
}
