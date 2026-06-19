package vn.com.be_crm.domain.quotation.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.quotation.enums.QuotationStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Domain entity đại diện cho báo giá.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Quotation {
    /** ID báo giá. */
    private Long id;
    /** Mã báo giá. */
    private String code;
    /** ID khách hàng. */
    private Long customerId;
    /** ID liên hệ. */
    private Long contactId;
    /** Cơ hội liên quan (truy vết nguồn). */
    private Long opportunityId;
    /** ID người phụ trách. */
    private Long ownerId;
    /** Ngày báo giá. */
    private LocalDate quoteDate;
    /** Hiệu lực đến ngày. */
    private LocalDate validUntil;
    /** Loại tiền tệ (mặc định VND). */
    private String currency;
    /** Tỷ giá quy đổi. */
    private BigDecimal exchangeRate;
    /** Trạng thái báo giá. */
    private QuotationStatus status;
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
