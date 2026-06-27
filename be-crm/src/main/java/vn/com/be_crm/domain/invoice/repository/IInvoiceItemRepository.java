package vn.com.be_crm.domain.invoice.repository;

import vn.com.be_crm.domain.invoice.entity.InvoiceItem;

import java.util.List;
import java.util.Optional;

/**
 * Port lưu trữ cho InvoiceItem.
 */
public interface IInvoiceItemRepository {
    /** Lưu mới hoặc cập nhật dòng đơn hàng. @param i @return entity sau khi lưu */
    InvoiceItem save(InvoiceItem i);
    /** Tìm dòng đơn hàng theo ID. @param id @return Optional */
    Optional<InvoiceItem> findById(Long id);
    /** Xóa dòng đơn hàng. @param id */
    void deleteById(Long id);
    /** Lấy danh sách dòng đơn hàng theo invoiceId. @param invoiceId @return danh sách */
    List<InvoiceItem> findAllByInvoiceId(Long invoiceId);
}
