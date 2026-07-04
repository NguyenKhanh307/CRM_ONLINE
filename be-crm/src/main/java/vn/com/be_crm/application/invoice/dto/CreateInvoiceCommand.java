package vn.com.be_crm.application.invoice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/** Input DTO khi tạo mới hóa đơn (kèm dòng hàng nếu có). */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateInvoiceCommand {
    @NotBlank(message = "Mã hóa đơn không được để trống") @Size(max = 20) private String code;
    private Long customerId;
    private Long contactId;
    private Long quotationId;
    private Long opportunityId;
    private Long orderId;
    private Long campaignId;
    private Long ownerId;
    private LocalDate invoiceDate;
    private LocalDate dueDate;
    @Size(max = 3) private String currency;
    private BigDecimal exchangeRate;
    @Size(max = 255) private String billingAddress;
    @Size(max = 15) private String taxCode;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal tax;
    private BigDecimal total;
    @Size(max = 255) private String note;
    /** Dòng hàng tạo kèm hóa đơn (invoiceId bỏ trống). */
    @Valid private List<CreateInvoiceItemCommand> items;
}
