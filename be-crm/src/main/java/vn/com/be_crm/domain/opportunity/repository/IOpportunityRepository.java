package vn.com.be_crm.domain.opportunity.repository;

import vn.com.be_crm.application.shared.dto.DeletedItemResult;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.domain.opportunity.entity.Opportunity;
import vn.com.be_crm.domain.opportunity.entity.OpportunityItem;

import java.util.List;
import java.util.Optional;

/**
 * Port lưu trữ cho Opportunity.
 */
public interface IOpportunityRepository {

    /**
     * Lưu mới hoặc cập nhật cơ hội bán hàng.
     * @param opportunity domain entity @return entity sau khi lưu
     */
    Opportunity save(Opportunity opportunity);

    /**
     * Tìm cơ hội theo mã (code) chưa xóa mềm — dùng cho import UPDATE/BOTH.
     * @param code mã cơ hội @return Optional
     */
    java.util.Optional<Opportunity> findByCode(String code);

    /**
     * Lưu cơ hội kèm danh sách dòng hàng trong một transaction.
     * @param opportunity domain entity cơ hội
     * @param items       danh sách dòng hàng (opportunityId sẽ được gán sau khi lưu cơ hội)
     * @return cơ hội sau khi lưu
     */
    Opportunity saveWithItems(Opportunity opportunity, List<OpportunityItem> items);

    /**
     * Tìm cơ hội theo ID (chưa xóa mềm).
     * @param id ID @return Optional
     */
    Optional<Opportunity> findById(Long id);

    /**
     * Xóa mềm cơ hội theo ID, ghi nhận người xóa.
     * @param id        ID cần xóa
     * @param deletedBy ID người thực hiện xóa
     */
    void deleteById(Long id, Long deletedBy);

    /**
     * Lấy danh sách cơ hội chưa xóa có phân trang.
     * @param r tham số phân trang @return PageResult
     */
    PageResult<Opportunity> findAll(PageRequest r);

    /**
     * Lấy danh sách cơ hội đã xóa mềm trong 30 ngày gần nhất (dùng cho thùng rác).
     * @param userId  ID người dùng hiện tại
     * @param isAdmin true nếu admin (xem tất cả), false nếu chỉ xem của mình
     * @param r       tham số phân trang
     * @return PageResult<DeletedItemResult>
     */
    PageResult<DeletedItemResult> findDeleted(Long userId, boolean isAdmin, PageRequest r);

    /**
     * Khôi phục cơ hội từ thùng rác.
     * @param id ID cần khôi phục
     */
    void restoreById(Long id);

    /**
     * Ẩn cơ hội khỏi thùng rác (set is_purged = true).
     * @param id ID cần ẩn
     */
    void purgeById(Long id);

    /**
     * Bàn giao hàng loạt cơ hội sang người dùng mới.
     * @param ids              danh sách ID cần bàn giao
     * @param toUserId         ID người nhận
     * @param currentUserId    ID người thực hiện
     * @param isAdminOrManager true → bàn giao bất kỳ; false → chỉ bàn giao bản ghi mình là owner
     */
    void handoverBulk(List<Long> ids, Long toUserId, Long currentUserId, boolean isAdminOrManager);

    /**
     * Bàn giao toàn bộ cơ hội của fromUserId sang toUserId.
     * @param fromUserId ID người bàn giao
     * @param toUserId   ID người nhận
     */
    void handoverAll(Long fromUserId, Long toUserId);
}
