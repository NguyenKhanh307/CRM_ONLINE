package vn.com.be_crm.application.lead.query;

import vn.com.be_crm.core.dto.delete.DeletedItemResult;
import vn.com.be_crm.core.page.PageRequest;
import vn.com.be_crm.core.page.PageResult;
import vn.com.be_crm.domain.lead.repository.ILeadRepository;

// lấy danh sách tiềm năng đã xóa (thùng rác), 30 ngày gần nhất
public class ListDeletedLeadsUseCase {
    private final ILeadRepository repo;

    public ListDeletedLeadsUseCase(ILeadRepository repo) { this.repo = repo; }

    // isAdmin=true xem tất cả, false chỉ xem bản ghi mình đã xóa
    public PageResult<DeletedItemResult> execute(Long userId, boolean isAdmin, PageRequest req) {
        return repo.findDeleted(userId, isAdmin, req);
    }
}
