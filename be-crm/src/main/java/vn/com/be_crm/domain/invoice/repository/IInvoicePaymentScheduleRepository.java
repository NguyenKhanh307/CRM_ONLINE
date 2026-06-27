package vn.com.be_crm.domain.invoice.repository;

import vn.com.be_crm.domain.invoice.entity.InvoicePaymentSchedule;

import java.util.List;
import java.util.Optional;

/**
 * Port lưu trữ cho InvoicePaymentSchedule.
 */
public interface IInvoicePaymentScheduleRepository {
    /** Lưu mới hoặc cập nhật đợt thanh toán. @param s @return entity sau khi lưu */
    InvoicePaymentSchedule save(InvoicePaymentSchedule s);
    /** Tìm đợt thanh toán theo ID. @param id @return Optional */
    Optional<InvoicePaymentSchedule> findById(Long id);
    /** Xóa đợt thanh toán. @param id */
    void deleteById(Long id);
    /** Lấy danh sách đợt thanh toán theo invoiceId. @param invoiceId @return danh sách */
    List<InvoicePaymentSchedule> findAllByInvoiceId(Long invoiceId);
}
