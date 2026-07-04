package vn.com.be_crm.domain.campaign.repository;

import vn.com.be_crm.application.campaign.dto.CampaignStatsResult;
import vn.com.be_crm.application.shared.dto.DeletedItemResult;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.domain.campaign.entity.Campaign;

import java.util.Optional;

/**
 * Port lưu trữ cho Campaign.
 */
public interface ICampaignRepository {

    /** Lưu mới hoặc cập nhật chiến dịch. @param c @return entity sau khi lưu */
    Campaign save(Campaign c);

    /** Tìm chiến dịch theo mã (chưa xóa mềm) — dùng cho import UPDATE/BOTH. @param code @return Optional */
    Optional<Campaign> findByCode(String code);

    /** Tìm chiến dịch theo ID. @param id @return Optional */
    Optional<Campaign> findById(Long id);

    /**
     * Xóa mềm chiến dịch theo ID, ghi nhận người xóa.
     * @param id ID cần xóa @param deletedBy ID người thực hiện xóa
     */
    void deleteById(Long id, Long deletedBy);

    /** Lấy danh sách chiến dịch có phân trang. @param r @return PageResult */
    PageResult<Campaign> findAll(PageRequest r);

    /**
     * Lấy danh sách chiến dịch đã xóa mềm trong 30 ngày gần nhất.
     * @param userId ID người dùng hiện tại @param isAdmin true nếu admin @param r phân trang
     */
    PageResult<DeletedItemResult> findDeleted(Long userId, boolean isAdmin, PageRequest r);

    /** Khôi phục chiến dịch từ thùng rác. @param id */
    void restoreById(Long id);

    /** Ẩn chiến dịch khỏi thùng rác. @param id */
    void purgeById(Long id);

    /**
     * Bàn giao hàng loạt chiến dịch sang người dùng mới.
     * @param ids danh sách ID @param toUserId người nhận @param currentUserId người thực hiện @param isAdminOrManager quyền
     */
    void handoverBulk(java.util.List<Long> ids, Long toUserId, Long currentUserId, boolean isAdminOrManager);

    /** Bàn giao toàn bộ chiến dịch của fromUserId sang toUserId. @param fromUserId @param toUserId */
    void handoverAll(Long fromUserId, Long toUserId);

    /**
     * Tính chỉ số hiệu quả (ROI) của chiến dịch: số thành viên, số tiềm năng, số cơ hội thắng, doanh thu.
     * @param campaignId ID chiến dịch @return kết quả thống kê
     */
    CampaignStatsResult getStats(Long campaignId);
}
