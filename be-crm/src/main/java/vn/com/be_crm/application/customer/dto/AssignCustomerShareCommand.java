package vn.com.be_crm.application.customer.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.customer.enums.CustomerSharePermission;

/** Input DTO khi gán chia sẻ khách hàng. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class AssignCustomerShareCommand {
    @NotNull private Long customerId;
    @NotNull private Long userId;
    private CustomerSharePermission permission;
    private Long sharedBy;
}
