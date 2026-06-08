package vn.com.be_crm.application.customer.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** Input DTO khi tạo mới dòng kiểm kho. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateInventoryCheckItemCommand {
    @NotNull(message = "Phiếu kiểm kho không được để trống") private Long checkId;
    private Long productId;
    private BigDecimal quantity;
    @Size(max = 255) private String note;
}
