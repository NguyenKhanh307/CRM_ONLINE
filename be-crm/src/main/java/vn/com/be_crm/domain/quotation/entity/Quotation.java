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
    private Long id;
    private String code;
    private Long customerId;
    private Long contactId;
    private Long ownerId;
    private LocalDate quoteDate;
    private LocalDate validUntil;
    private QuotationStatus status;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal tax;
    private BigDecimal total;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    /** Thời điểm xóa mềm. */
    private LocalDateTime deletedAt;
}
