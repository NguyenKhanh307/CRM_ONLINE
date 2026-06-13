package vn.com.be_crm.application.order.dto;

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

/** Output DTO cho Order. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class OrderResult {
    private Long id;
    private String code;
    private Long customerId;
    private Long contactId;
    private Long quotationId;
    private Long opportunityId;
    private Long ownerId;
    private Long executorUnitId;
    private Long warehouseId;
    private Long parentOrderId;
    private OrderType orderType;
    private LocalDate orderDate;
    private String currency;
    private BigDecimal exchangeRate;
    private OrderStatus status;
    private PaymentStatus paymentStatus;
    private Integer creditDays;
    private LocalDate paymentDueDate;
    private boolean isInvoiced;
    private String receiverName;
    private String receiverPhone;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal tax;
    private BigDecimal total;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
