package vn.com.be_crm.application.customer.query;

import vn.com.be_crm.core.dto.delete.DeletedItemResult;
import vn.com.be_crm.core.page.PageRequest;
import vn.com.be_crm.core.page.PageResult;
import vn.com.be_crm.domain.customer.repository.ICustomerRepository;

/** Use case lấy danh sách khách hàng đã xóa (thùng rác). */
public class ListDeletedCustomersUseCase {
    private final ICustomerRepository repo;
    /** @param repo port lưu trữ */
    public ListDeletedCustomersUseCase(ICustomerRepository repo) { this.repo = repo; }

    /**
     * Lấy danh sách khách hàng trong thùng rác (30 ngày gần nhất).
     * @param userId  ID người dùng hiện tại
     * @param isAdmin true nếu admin
     * @param req     tham số phân trang
     */
    public PageResult<DeletedItemResult> execute(Long userId, boolean isAdmin, PageRequest req) {
        return repo.findDeleted(userId, isAdmin, req);
    }
}
