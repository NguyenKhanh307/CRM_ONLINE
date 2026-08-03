package vn.com.be_crm.application.quotation.query;

import vn.com.be_crm.core.dto.delete.DeletedItemResult;
import vn.com.be_crm.core.page.PageRequest;
import vn.com.be_crm.core.page.PageResult;
import vn.com.be_crm.domain.quotation.repository.IQuotationRepository;

/** Use case lấy danh sách báo giá đã xóa (thùng rác). */
public class ListDeletedQuotationsUseCase {
    private final IQuotationRepository repo;
    /** @param repo port lưu trữ */
    public ListDeletedQuotationsUseCase(IQuotationRepository repo) { this.repo = repo; }

    /**
     * Lấy danh sách báo giá trong thùng rác (30 ngày gần nhất).
     * @param userId  ID người dùng hiện tại
     * @param isAdmin true nếu admin
     * @param req     tham số phân trang
     */
    public PageResult<DeletedItemResult> execute(Long userId, boolean isAdmin, PageRequest req) {
        return repo.findDeleted(userId, isAdmin, req);
    }
}
