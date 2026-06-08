package vn.com.be_crm.application.customer.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.customer.enums.InventoryCheckStatus;

import java.time.LocalDate;

/** Input DTO khi cập nhật phiếu kiểm kho. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class UpdateInventoryCheckCommand {
    private Long id;
    private Long checkedBy;
    private LocalDate checkDate;
    private InventoryCheckStatus status;
    @Size(max = 255) private String note;
}
