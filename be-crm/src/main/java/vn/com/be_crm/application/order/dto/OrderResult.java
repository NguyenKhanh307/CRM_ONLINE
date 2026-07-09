package vn.com.be_crm.application.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.be_crm.domain.order.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Output DTO cho Order. */
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class OrderResult {
    private Long id;
    private String code;
    private Long customerId;
    private Long contactId;
    private Long quotationId;
    private Long opportunityId;
    private Long campaignId;
    private Long ownerId;
    private LocalDate orderDate;
    private LocalDate deliveryDate;
    private String currency;
    private BigDecimal exchangeRate;
    private OrderStatus status;
    private Boolean isLocked;
    private String billingAddress;
    private String taxCode;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal tax;
    private BigDecimal total;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // Tên khóa ngoại — do BE resolve (INameResolver).
    private String customerName;
    private String contactName;
    private String quotationCode;
    private String opportunityName;
    private String campaignName;
    private String ownerName;
}
