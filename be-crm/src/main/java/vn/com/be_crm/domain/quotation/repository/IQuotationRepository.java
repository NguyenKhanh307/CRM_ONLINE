package vn.com.be_crm.domain.quotation.repository;

import vn.com.be_crm.application.shared.dto.DeletedItemResult;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.domain.quotation.entity.Quotation;

import java.util.Optional;

/**
 * Port lưu trữ cho Quotation.
 */
public interface IQuotationRepository {

    /** Lưu mới hoặc cập nhật báo giá. @param q @return entity sau khi lưu */
    Quotation save(Quotation q);

    /** Tìm báo giá theo ID. @param id @return Optional */
    Optional<Quotation> findById(Long id);

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
}
