package vn.com.be_crm.application.quotation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.quotation.enums.QuotationStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Input DTO khi tạo mới báo giá. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateQuotationCommand {
    @NotBlank(message = "Mã báo giá không được để trống") @Size(max = 20) private String code;
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
    @Size(max = 255) private String note;
}
