package vn.com.be_crm.domain.quotation.repository;

import vn.com.be_crm.domain.quotation.entity.QuotationItem;

import java.util.List;
import java.util.Optional;

/**
 * Port lưu trữ cho QuotationItem.
 */
public interface IQuotationItemRepository {
    /** Lưu mới hoặc cập nhật dòng báo giá. @param item @return entity sau khi lưu */
    QuotationItem save(QuotationItem item);
    /** Tìm dòng báo giá theo ID. @param id @return Optional */
    Optional<QuotationItem> findById(Long id);
    /** Xóa dòng báo giá theo ID. @param id */
    void deleteById(Long id);
    /** Lấy danh sách dòng báo giá theo quotationId. @param quotationId @return danh sách */
    List<QuotationItem> findAllByQuotationId(Long quotationId);
}
