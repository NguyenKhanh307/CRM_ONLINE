package vn.com.be_crm.domain.order.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.order.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Domain entity đại diện cho đơn hàng (chốt bán, sinh từ báo giá, tiền đề của hóa đơn).
 */
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    /** ID đơn hàng. */
    private Long id;
    /** Mã đơn hàng. */
    private String code;
    /** ID khách hàng. */
    private Long customerId;
    /** ID liên hệ. */
    private Long contactId;
    /** Từ báo giá. */
    private Long quotationId;
    /** Từ cơ hội. */
    private Long opportunityId;
    /** Chiến dịch nguồn (attribution). */
    private Long campaignId;
    /** ID người phụ trách. */
    private Long ownerId;
    /** Ngày đơn hàng. */
    private LocalDate orderDate;
    /** Ngày giao dự kiến. */
    private LocalDate deliveryDate;
    /** Loại tiền tệ (mặc định VND). */
    private String currency;
    /** Tỷ giá quy đổi. */
    private BigDecimal exchangeRate;
    /** Trạng thái đơn hàng. */
    private OrderStatus status;
    /** Khóa dữ liệu khi đã xuất hóa đơn (read-only). */
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
