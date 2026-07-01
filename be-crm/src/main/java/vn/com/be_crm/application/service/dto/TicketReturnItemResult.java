package vn.com.be_crm.application.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.service.enums.ReturnReason;

import java.math.BigDecimal;

/** Output DTO cho TicketReturnItem. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class TicketReturnItemResult {
    private Long id;
    private Long ticketId;
    private Long invoiceItemId;
    private Long productId;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal amount;
    private ReturnReason reason;
    private String conditionNote;
}
