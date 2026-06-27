package vn.com.be_crm.application.customer.command;

import vn.com.be_crm.application.customer.dto.CustomerResult;
import vn.com.be_crm.application.customer.mapper.CustomerCommandMapper;
import vn.com.be_crm.domain.customer.entity.Customer;
import vn.com.be_crm.domain.customer.enums.CustomerStatus;
import vn.com.be_crm.domain.customer.repository.ICustomerRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/**
 * Use case điều phối trạng thái khách hàng (theo hành động, không sửa tay):
 * activate (→ active) / deactivate (→ inactive).
 */
public class CustomerWorkflowUseCase {
    private final ICustomerRepository repo;

    /** @param repo port lưu trữ Customer */
    public CustomerWorkflowUseCase(ICustomerRepository repo) { this.repo = repo; }

    /** Kích hoạt khách hàng (→ active). @param id ID @return khách hàng sau cập nhật */
    public CustomerResult activate(Long id) { return transition(id, CustomerStatus.active); }

    /** Ngừng hoạt động khách hàng (→ inactive). @param id ID @return khách hàng sau cập nhật */
    public CustomerResult deactivate(Long id) { return transition(id, CustomerStatus.inactive); }

    /** Thực hiện một bước chuyển trạng thái có kiểm tra hợp lệ. */
    private CustomerResult transition(Long id, CustomerStatus target) {
        Customer c = repo.findById(id).orElseThrow(() -> new NotFoundException("Customer not found: " + id));
        c.getStatus().ensureCanTransitionTo(target);
        return CustomerCommandMapper.toResult(repo.save(c.toBuilder().status(target).build()));
    }
}
