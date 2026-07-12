package vn.com.be_crm.application.opportunity.query;

import vn.com.be_crm.application.opportunity.dto.BoardColumnResult;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityBoardRepository;

import java.util.List;

/** Use case nạp bảng Kanban cơ hội (cột = giai đoạn pipeline, thẻ = cơ hội). */
public class GetOpportunityBoardUseCase {

    private final IOpportunityBoardRepository repo;

    /** @param repo port đọc dữ liệu bảng Kanban */
    public GetOpportunityBoardUseCase(IOpportunityBoardRepository repo) {
        this.repo = repo;
    }

    /**
     * @param ownerId            null = xem tất cả (admin/manager); ngược lại chỉ cơ hội của nhân viên này
     * @param dataAccessFromYear năm sớm nhất được xem
     * @param q                  từ khóa tìm kiếm theo mã/tên cơ hội
     * @return danh sách cột theo thứ tự giai đoạn
     */
    public List<BoardColumnResult> execute(Long ownerId, Integer dataAccessFromYear, String q) {
        return repo.getBoard(ownerId, dataAccessFromYear, q);
    }
}
