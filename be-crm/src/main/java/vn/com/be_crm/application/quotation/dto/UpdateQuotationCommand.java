package vn.com.be_crm.application.quotation.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.quotation.enums.QuotationStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Input DTO khi cập nhật báo giá. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class UpdateQuotationCommand {
    private Long id;
    private Long customerId;
    private Long contactId;
    private Long opportunityId;
    private Long ownerId;
    private LocalDate quoteDate;
    private LocalDate validUntil;
    @Size(max = 3) private String currency;
    private BigDecimal exchangeRate;
    private QuotationStatus status;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal tax;
    private BigDecimal total;
    @Size(max = 255) private String note;
}
