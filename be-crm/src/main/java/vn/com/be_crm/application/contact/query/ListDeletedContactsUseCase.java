package vn.com.be_crm.application.contact.query;

import vn.com.be_crm.core.dto.delete.DeletedItemResult;
import vn.com.be_crm.core.page.PageRequest;
import vn.com.be_crm.core.page.PageResult;
import vn.com.be_crm.domain.contact.repository.IContactRepository;

/** Use case lấy danh sách liên hệ đã xóa (thùng rác). */
public class ListDeletedContactsUseCase {
    private final IContactRepository repo;
    /** @param repo port lưu trữ */
    public ListDeletedContactsUseCase(IContactRepository repo) { this.repo = repo; }

    /**
     * Lấy danh sách liên hệ trong thùng rác (30 ngày gần nhất).
     * @param userId  ID người dùng hiện tại
     * @param isAdmin true nếu admin
     * @param req     tham số phân trang
     */
    public PageResult<DeletedItemResult> execute(Long userId, boolean isAdmin, PageRequest req) {
        return repo.findDeleted(userId, isAdmin, req);
    }
}
