package vn.com.be_crm.application.customer.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.customer.enums.CustomerStatus;
import vn.com.be_crm.domain.customer.enums.CustomerType;

/** Input DTO khi cập nhật khách hàng. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class UpdateCustomerCommand {
    private Long id;
    @Size(max = 100) private String name;
    private CustomerType type;
    @Size(max = 15) private String taxCode;
    @Size(max = 11) private String phone;
    @Size(max = 50) private String email;
    @Size(max = 255) private String address;
    @Size(max = 20) private String source;
    private CustomerStatus status;
    private Long ownerId;
    private Long unitId;
}
