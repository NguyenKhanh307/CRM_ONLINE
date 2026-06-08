package vn.com.be_crm.application.customer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.customer.enums.InventoryCheckStatus;

import java.time.LocalDate;

/** Input DTO khi tạo mới phiếu kiểm kho. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateInventoryCheckCommand {
    @NotBlank(message = "Mã phiếu không được để trống") @Size(max = 20) private String code;
    @NotNull(message = "Khách hàng không được để trống") private Long customerId;
    private Long checkedBy;
    private LocalDate checkDate;
    private InventoryCheckStatus status;
    @Size(max = 255) private String note;
}
