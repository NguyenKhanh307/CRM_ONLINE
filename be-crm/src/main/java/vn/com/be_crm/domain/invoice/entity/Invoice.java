package vn.com.be_crm.domain.invoice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.invoice.enums.InvoiceStatus;
import vn.com.be_crm.domain.invoice.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Domain entity đại diện cho hóa đơn (chứng từ tài chính cuối cùng, kế thừa từ báo giá).
 */
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {
    /** ID hóa đơn. */
    private Long id;
    /** Mã hóa đơn. */
    private String code;
    /** ID khách hàng. */
    private Long customerId;
    /** ID liên hệ. */
    private Long contactId;
    /** Từ báo giá. */
    private Long quotationId;
    /** Từ cơ hội. */
    private Long opportunityId;
    /** Từ đơn hàng (1-1). */
    private Long orderId;
    /** Chiến dịch nguồn (attribution). */
    private Long campaignId;
    /** ID người phụ trách. */
    private Long ownerId;
    /** Ngày hóa đơn. */
    private LocalDate invoiceDate;
    /** Hạn thanh toán. */
    private LocalDate dueDate;
    /** Loại tiền tệ (mặc định VND). */
    private String currency;
    /** Tỷ giá quy đổi. */
    private BigDecimal exchangeRate;
    /** Trạng thái hóa đơn. */
    private InvoiceStatus status;
    /** Trạng thái thanh toán (suy ra từ các đợt thanh toán). */
    private PaymentStatus paymentStatus;
    /** Khóa dữ liệu khi đã phát hành (read-only). */
    private boolean isLocked;
    /** Địa chỉ xuất hóa đơn. */
    private String billingAddress;
    /** Mã số thuế. */
    private String taxCode;
    /** Tạm tính. */
    private BigDecimal subtotal;
    /** Chiết khấu. */
    private BigDecimal discount;
    /** Thuế. */
    private BigDecimal tax;
    /** Tổng cộng. */
    private BigDecimal total;
    /** Ghi chú. */
    private String note;
    /** Thời điểm tạo. */
    private LocalDateTime createdAt;
    /** Thời điểm cập nhật gần nhất. */
    private LocalDateTime updatedAt;
    /** Thời điểm xóa mềm. */
    private LocalDateTime deletedAt;
    /** ID người dùng đã xóa. */
    private Long deletedBy;
    /** True nếu đã ẩn khỏi thùng rác. */
    private boolean isPurged;
}
