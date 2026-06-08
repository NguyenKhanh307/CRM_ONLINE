package vn.com.be_crm.domain.quotation.repository;

import vn.com.be_crm.domain.quotation.entity.QuotationApproval;

import java.util.List;
import java.util.Optional;

/**
 * Port lưu trữ cho QuotationApproval.
 */
public interface IQuotationApprovalRepository {
    /** Lưu mới hoặc cập nhật bước phê duyệt. @param approval @return entity sau khi lưu */
    QuotationApproval save(QuotationApproval approval);
    /** Tìm bước phê duyệt theo ID. @param id @return Optional */
    Optional<QuotationApproval> findById(Long id);
    /** Xóa bước phê duyệt theo ID. @param id */
    void deleteById(Long id);
    /** Lấy danh sách bước phê duyệt theo quotationId. @param quotationId @return danh sách */
    List<QuotationApproval> findAllByQuotationId(Long quotationId);
}
