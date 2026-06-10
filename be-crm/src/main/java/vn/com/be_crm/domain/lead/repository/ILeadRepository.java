package vn.com.be_crm.domain.lead.repository;

import vn.com.be_crm.application.shared.dto.DeletedItemResult;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.domain.lead.entity.Lead;

import java.util.Optional;

/**
 * Port lưu trữ cho Lead.
 */
public interface ILeadRepository {

    /**
     * Lưu mới hoặc cập nhật tiềm năng.
     * @param lead domain entity @return entity sau khi lưu
     */
    Lead save(Lead lead);

    /**
     * Tìm tiềm năng theo ID (chưa xóa mềm).
     * @param id ID @return Optional
     */
    Optional<Lead> findById(Long id);

    /**
     * Xóa mềm tiềm năng theo ID, ghi nhận người xóa.
     * @param id        ID cần xóa
     * @param deletedBy ID người thực hiện xóa
     */
    void deleteById(Long id, Long deletedBy);

    /**
     * Lấy danh sách tiềm năng chưa xóa có phân trang.
     * @param r tham số phân trang @return PageResult
     */
    PageResult<Lead> findAll(PageRequest r);

    /**
     * Lấy danh sách tiềm năng đã xóa mềm trong 30 ngày gần nhất (dùng cho thùng rác).
     * @param userId  ID người dùng hiện tại
     * @param isAdmin true nếu admin (xem tất cả), false nếu chỉ xem của mình
     * @param r       tham số phân trang
     * @return PageResult<DeletedItemResult>
     */
    PageResult<DeletedItemResult> findDeleted(Long userId, boolean isAdmin, PageRequest r);

    /**
     * Khôi phục tiềm năng từ thùng rác (xóa mềm → active).
     * @param id ID cần khôi phục
     */
    void restoreById(Long id);

    /**
     * Ẩn tiềm năng khỏi thùng rác (set is_purged = true, DB vẫn giữ soft-delete).
     * @param id ID cần ẩn
     */
    void purgeById(Long id);
}
