package vn.com.be_crm.domain.invoice.repository;

import vn.com.be_crm.domain.invoice.entity.InvoiceRevenueRecord;

import java.util.List;
import java.util.Optional;

/**
 * Port lưu trữ cho InvoiceRevenueRecord.
 */
public interface IInvoiceRevenueRecordRepository {
    /** Lưu bản ghi doanh thu. @param r @return entity sau khi lưu */
    InvoiceRevenueRecord save(InvoiceRevenueRecord r);
    /** Tìm bản ghi doanh thu theo ID. @param id @return Optional */
    Optional<InvoiceRevenueRecord> findById(Long id);
    /** Xóa bản ghi doanh thu. @param id */
    void deleteById(Long id);
    /** Lấy danh sách bản ghi doanh thu theo invoiceId. @param invoiceId @return danh sách */
    List<InvoiceRevenueRecord> findAllByInvoiceId(Long invoiceId);
}
