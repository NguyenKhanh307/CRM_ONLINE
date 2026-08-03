package vn.com.be_crm.application.customer.command;

import vn.com.be_crm.application.customer.dto.CustomerResult;
import vn.com.be_crm.application.customer.dto.UpdateCustomerCommand;
import vn.com.be_crm.application.customer.mapper.CustomerCommandMapper;
import vn.com.be_crm.core.notify.NotifyAssignmentUseCase;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.customer.entity.Customer;
import vn.com.be_crm.domain.customer.repository.ICustomerRepository;
import vn.com.be_crm.core.error.frontend.NotFoundException;

import java.util.Objects;

/** Use case cập nhật khách hàng. */
public class UpdateCustomerUseCase implements IUseCase<UpdateCustomerCommand, CustomerResult> {
    private final ICustomerRepository repo;
    private final NotifyAssignmentUseCase notifyUC;
    /** @param repo port lưu trữ @param notifyUC báo cho người phụ trách mới */
    public UpdateCustomerUseCase(ICustomerRepository repo, NotifyAssignmentUseCase notifyUC) {
        this.repo = repo;
        this.notifyUC = notifyUC;
    }

    /**
     * Cập nhật Customer và trả về result.
     * @param cmd dữ liệu cập nhật @return CustomerResult
     * @throws NotFoundException nếu không tìm thấy
     */
    @Override
    public CustomerResult execute(UpdateCustomerCommand cmd) {
        Customer existing = repo.findById(cmd.getId())
                .orElseThrow(() -> new NotFoundException("Customer not found: " + cmd.getId()));
        Customer saved = repo.save(CustomerCommandMapper.toEntity(cmd, existing));
        // Đổi người phụ trách → báo cho người nhận việc
        if (!Objects.equals(existing.getOwnerId(), saved.getOwnerId())) {
            notifyUC.notifyAssigned("customer", "khách hàng", saved.getOwnerId(), saved.getId(), saved.getCode());
        }
        return CustomerCommandMapper.toResult(saved);
    }
}
