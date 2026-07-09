package vn.com.be_crm.application.quotation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.be_crm.domain.quotation.enums.QuotationStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Output DTO cho Quotation. */
@Getter @Setter @Builder(toBuilder = true) @NoArgsConstructor @AllArgsConstructor
public class QuotationResult {
    private Long id;
    private String code;
    private Long customerId;
    private Long contactId;
    private Long opportunityId;
    private Long campaignId;
    private Long pricePolicyId;
    private boolean isPrimary;
    private boolean isLocked;
    private Long ownerId;
    private LocalDate quoteDate;
    private LocalDate validUntil;
    private String currency;
    private BigDecimal exchangeRate;
    private QuotationStatus status;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal tax;
    private BigDecimal total;
    private String note;
    private String customerResponse;
    private String customerResponseNote;
    private LocalDateTime customerRespondedAt;
    /** Email đã gửi báo giá tới (chỉ điền khi gọi hành động send) — để FE hiển thị xác nhận. */
    private String sentToEmail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // Tên khóa ngoại — do BE resolve (INameResolver).
    private String customerName;
    private String contactName;
    private String opportunityName;
    private String ownerName;
}
