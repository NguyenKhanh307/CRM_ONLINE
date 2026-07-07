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
@Builder(toBuilder = true)
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
    /** Chiến dịch nguồn (attribution) — kế thừa từ cơ hội khi clone. */
    private Long campaignId;
    /** ID chính sách giá áp dụng (kế thừa từ cơ hội). */
    private Long pricePolicyId;
    /** Báo giá đồng bộ (primary) với cơ hội — chỉ một báo giá primary/cơ hội. */
    private boolean isPrimary;
    /** Khóa dữ liệu (read-only) sau khi đã chuyển thành hóa đơn. */
    private boolean isLocked;
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
    /** Token công khai cho link phản hồi của khách (gửi kèm email). */
    private String responseToken;
    /** Phản hồi của khách: accepted | adjust | rejected. */
    private String customerResponse;
    /** Nội dung điều chỉnh / lý do khách nhập khi phản hồi. */
    private String customerResponseNote;
    /** Thời điểm khách phản hồi. */
    private LocalDateTime customerRespondedAt;
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
