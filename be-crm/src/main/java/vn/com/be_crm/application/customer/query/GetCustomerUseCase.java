package vn.com.be_crm.application.customer.query;

import vn.com.be_crm.application.customer.dto.CustomerResult;
import vn.com.be_crm.application.customer.mapper.CustomerCommandMapper;
import vn.com.be_crm.application.shared.lookup.INameResolver;
import vn.com.be_crm.application.shared.lookup.NameEnricher;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.customer.repository.ICustomerRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

import java.util.List;

/** Use case lấy khách hàng theo ID — kèm tên khóa ngoại để trang chi tiết 360° hiển thị trực tiếp. */
public class GetCustomerUseCase implements IUseCase<Long, CustomerResult> {
    private final ICustomerRepository repo;
    private final INameResolver names;

    /** @param repo port lưu trữ @param names port tra tên khóa ngoại */
    public GetCustomerUseCase(ICustomerRepository repo, INameResolver names) {
        this.repo = repo;
        this.names = names;
    }

    /**
     * Lấy Customer theo ID.
     * @param id ID @return CustomerResult (đã điền ownerName/unitName)
     * @throws NotFoundException nếu không tìm thấy
     */
    @Override
    public CustomerResult execute(Long id) {
        CustomerResult result = CustomerCommandMapper.toResult(
                repo.findById(id).orElseThrow(() -> new NotFoundException("Customer not found: " + id)));
        List<CustomerResult> one = List.of(result);
        NameEnricher.apply(one, CustomerResult::getOwnerId, names::users, CustomerResult::setOwnerName);
        NameEnricher.apply(one, CustomerResult::getUnitId, names::orgUnits, CustomerResult::setUnitName);
        NameEnricher.apply(one, CustomerResult::getCreatedBy, names::users, CustomerResult::setCreatedByName);
        NameEnricher.apply(one, CustomerResult::getUpdatedBy, names::users, CustomerResult::setUpdatedByName);
        return result;
    }
}
