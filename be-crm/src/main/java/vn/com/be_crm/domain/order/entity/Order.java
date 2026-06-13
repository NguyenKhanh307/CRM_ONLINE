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
    private Long id;
    private String code;
    private Long customerId;
    private Long contactId;
    /** Từ báo giá. */
    private Long quotationId;
    /** Từ cơ hội. */
    private Long opportunityId;
    private Long ownerId;
    private Long executorUnitId;
    private Long warehouseId;
    private Long parentOrderId;
    private OrderType orderType;
    private LocalDate orderDate;
    /** Loại tiền tệ (mặc định VND). */
    private String currency;
    /** Tỷ giá quy đổi. */
    private BigDecimal exchangeRate;
    private OrderStatus status;
    private PaymentStatus paymentStatus;
    /** Số ngày được nợ. */
    private Integer creditDays;
    /** Hạn thanh toán. */
    private LocalDate paymentDueDate;
    /** Đã xuất hóa đơn. */
    private boolean isInvoiced;
    /** Người nhận hàng. */
    private String receiverName;
    private String receiverPhone;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal tax;
    private BigDecimal total;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    /** Thời điểm xóa mềm. */
    private LocalDateTime deletedAt;
    /** ID người dùng đã xóa. */
    private Long deletedBy;
    /** True nếu đã ẩn khỏi thùng rác. */
    private boolean isPurged;
}
