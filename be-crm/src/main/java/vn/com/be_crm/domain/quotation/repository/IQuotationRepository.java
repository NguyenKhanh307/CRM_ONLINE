package vn.com.be_crm.domain.quotation.repository;

import vn.com.be_crm.core.dto.delete.DeletedItemResult;
import vn.com.be_crm.core.page.PageRequest;
import vn.com.be_crm.core.page.PageResult;
import vn.com.be_crm.domain.quotation.entity.Quotation;
import vn.com.be_crm.domain.quotation.entity.QuotationItem;

import java.util.List;
import java.util.Optional;

/**
 * Port lưu trữ cho Quotation.
 */
public interface IQuotationRepository {

    /** Lưu mới hoặc cập nhật báo giá. @param q @return entity sau khi lưu */
    Quotation save(Quotation q);

    /**
     * Tìm báo giá theo mã (code) chưa xóa mềm — dùng cho import UPDATE/BOTH.
     * @param code mã báo giá @return Optional
     */
    Optional<Quotation> findByCode(String code);

    /**
     * Tìm báo giá theo token phản hồi công khai (chưa xóa mềm) — dùng cho trang khách phản hồi.
     * @param token token phản hồi @return Optional
     */
    Optional<Quotation> findByResponseToken(String token);

    /**
     * Lưu báo giá kèm danh sách dòng hàng trong một transaction.
     * @param q     domain entity báo giá
     * @param items danh sách dòng hàng (quotationId sẽ được gán sau khi lưu báo giá)
     * @return báo giá sau khi lưu
     */
    Quotation saveWithItems(Quotation q, List<QuotationItem> items);

    /** Tìm báo giá theo ID. @param id @return Optional */
    Optional<Quotation> findById(Long id);

    /**
     * Lấy danh sách báo giá theo cơ hội (chưa xóa mềm) — dùng cho quản lý báo giá đồng bộ (primary).
     * @param opportunityId ID cơ hội @return danh sách báo giá
     */
    List<Quotation> findAllByOpportunityId(Long opportunityId);

    /**
     * Xóa mềm báo giá theo ID, ghi nhận người xóa.
     * @param id        ID cần xóa
     * @param deletedBy ID người thực hiện xóa
     */
    void deleteById(Long id, Long deletedBy);

    /** Lấy danh sách báo giá có phân trang. @param r @return PageResult */
    PageResult<Quotation> findAll(PageRequest r);

    /**
     * Lấy danh sách báo giá đã xóa mềm trong 30 ngày gần nhất.
     * @param userId  ID người dùng hiện tại
     * @param isAdmin true nếu admin
     * @param r       tham số phân trang
     */
    PageResult<DeletedItemResult> findDeleted(Long userId, boolean isAdmin, PageRequest r);

    /** Khôi phục báo giá từ thùng rác. @param id */
    void restoreById(Long id);

    /** Ẩn báo giá khỏi thùng rác. @param id */
    void purgeById(Long id);

    /**
     * Bàn giao hàng loạt báo giá sang người dùng mới.
     * @param ids              danh sách ID cần bàn giao
     * @param toUserId         ID người nhận
     * @param currentUserId    ID người thực hiện
     * @param isAdminOrManager true → bàn giao bất kỳ; false → chỉ bàn giao bản ghi mình là owner
     */
    void handoverBulk(List<Long> ids, Long toUserId, Long currentUserId, boolean isAdminOrManager);

    /**
     * Bàn giao toàn bộ báo giá của fromUserId sang toUserId.
     * @param fromUserId ID người bàn giao
     * @param toUserId   ID người nhận
     */
    void handoverAll(Long fromUserId, Long toUserId);
}
