package vn.com.be_crm.application.service.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.service.enums.ReturnReason;

import java.math.BigDecimal;

/** Input DTO khi tạo mới dòng hàng trả/đổi. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateTicketReturnItemCommand {
    /** ID phiếu — controller set từ path; bỏ trống khi tạo nested kèm phiếu. */
    private Long ticketId;
    private Long invoiceItemId;
    private Long productId;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal amount;
    private ReturnReason reason;
    @Size(max = 255) private String conditionNote;
}
