package vn.com.be_crm.domain.order.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.order.enums.OrderStatus;
import vn.com.be_crm.domain.order.enums.OrderType;
import vn.com.be_crm.domain.order.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Domain entity đại diện cho đơn hàng.
 */
@Getter
@Builder
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
    /** ID người phụ trách. */
    private Long ownerId;
    /** ID đơn vị thực hiện. */
    private Long executorUnitId;
    /** ID kho xuất hàng. */
    private Long warehouseId;
    /** Đơn cha (nếu là đơn con). */
    private Long parentOrderId;
    /** Loại đơn (thường/cha/con). */
    private OrderType orderType;
    /** Ngày đặt hàng. */
    private LocalDate orderDate;
    /** Loại tiền tệ (mặc định VND). */
    private String currency;
    /** Tỷ giá quy đổi. */
    private BigDecimal exchangeRate;
    /** Trạng thái đơn hàng. */
    private OrderStatus status;
    /** Trạng thái thanh toán. */
    private PaymentStatus paymentStatus;
    /** Số ngày được nợ. */
    private Integer creditDays;
    /** Hạn thanh toán. */
    private LocalDate paymentDueDate;
    /** Đã xuất hóa đơn. */
    private boolean isInvoiced;
    /** Người nhận hàng. */
    private String receiverName;
    /** Số điện thoại người nhận. */
    private String receiverPhone;
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
