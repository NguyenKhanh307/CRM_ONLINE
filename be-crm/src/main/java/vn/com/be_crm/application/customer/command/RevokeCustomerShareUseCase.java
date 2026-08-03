package vn.com.be_crm.application.customer.command;

import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.customer.repository.ICustomerShareRepository;

/** Use case thu hồi chia sẻ khách hàng. */
public class RevokeCustomerShareUseCase implements IUseCase<long[], Void> {
    private final ICustomerShareRepository repo;
    /** @param repo port lưu trữ */
    public RevokeCustomerShareUseCase(ICustomerShareRepository repo) { this.repo = repo; }

    /**
     * Thu hồi chia sẻ khách hàng theo customerId và userId.
     * @param ids mảng [customerId, userId] @return null
     */
    @Override
    public Void execute(long[] ids) {
        repo.deleteByCustomerIdAndUserId(ids[0], ids[1]);
        return null;
    }
}
