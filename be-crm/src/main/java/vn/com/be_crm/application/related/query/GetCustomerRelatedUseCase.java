package vn.com.be_crm.application.related.query;

import vn.com.be_crm.application.related.dto.CustomerRelatedResult;
import vn.com.be_crm.domain.customer.entity.Customer;
import vn.com.be_crm.domain.customer.repository.ICustomerRepository;
import vn.com.be_crm.domain.related.repository.IRelatedRepository;
import vn.com.be_crm.core.error.frontend.ForbiddenException;
import vn.com.be_crm.core.error.frontend.NotFoundException;

/**
 * Lấy toàn bộ bản ghi liên quan của một khách hàng cho trang chi tiết 360°.
 * Quyền được kiểm tra MỘT LẦN trên khách hàng (bản ghi cha); qua được thì trả đủ bản ghi con
 * bất kể chúng thuộc về nhân viên nào.
 */
public class GetCustomerRelatedUseCase {

    private final ICustomerRepository customerRepo;
    private final IRelatedRepository relatedRepo;

    /** @param customerRepo port khách hàng @param relatedRepo port bản ghi liên quan */
    public GetCustomerRelatedUseCase(ICustomerRepository customerRepo, IRelatedRepository relatedRepo) {
        this.customerRepo = customerRepo;
        this.relatedRepo = relatedRepo;
    }

    /**
     * @param customerId ID khách hàng
     * @param userId     ID người đang đăng nhập
     * @param privileged true nếu ADMIN/SALES_MANAGER (xem mọi khách hàng)
     * @return các nhóm bản ghi liên quan
     */
    public CustomerRelatedResult execute(Long customerId, Long userId, boolean privileged) {
        Customer c = customerRepo.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer", customerId));
        if (!privileged && (c.getOwnerId() == null || !c.getOwnerId().equals(userId))) {
            throw new ForbiddenException("Bạn không có quyền xem khách hàng này");
        }
        return relatedRepo.getCustomerRelated(customerId);
    }
}
