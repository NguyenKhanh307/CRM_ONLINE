package vn.com.be_crm.application.customer.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** Input DTO khi cập nhật dòng kiểm kho. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class UpdateInventoryCheckItemCommand {
    private Long id;
    private Long productId;
    private BigDecimal quantity;
    @Size(max = 255) private String note;
}
