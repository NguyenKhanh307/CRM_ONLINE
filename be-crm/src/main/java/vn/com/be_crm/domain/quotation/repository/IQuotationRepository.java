package vn.com.be_crm.domain.quotation.repository;

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
    /** Xóa mềm báo giá. @param id */
    void deleteById(Long id);
    /** Lấy danh sách báo giá có phân trang. @param r @return PageResult */
    PageResult<Quotation> findAll(PageRequest r);
}
