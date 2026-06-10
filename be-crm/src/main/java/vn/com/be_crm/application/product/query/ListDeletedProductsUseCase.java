package vn.com.be_crm.application.product.query;

import vn.com.be_crm.application.shared.dto.DeletedItemResult;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.domain.product.repository.IProductRepository;

/** Use case lấy danh sách hàng hóa đã xóa (thùng rác). */
public class ListDeletedProductsUseCase {
    private final IProductRepository repo;
    /** @param repo port lưu trữ */
    public ListDeletedProductsUseCase(IProductRepository repo) { this.repo = repo; }

    /**
     * Lấy danh sách hàng hóa trong thùng rác (30 ngày gần nhất).
     * @param userId  ID người dùng hiện tại
     * @param isAdmin true nếu admin
     * @param req     tham số phân trang
     */
    public PageResult<DeletedItemResult> execute(Long userId, boolean isAdmin, PageRequest req) {
        return repo.findDeleted(userId, isAdmin, req);
    }
}
