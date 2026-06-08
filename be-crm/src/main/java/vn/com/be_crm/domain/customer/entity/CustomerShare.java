package vn.com.be_crm.domain.customer.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.customer.enums.CustomerSharePermission;

import java.time.LocalDateTime;

/**
 * Domain entity đại diện cho việc chia sẻ khách hàng với người dùng.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerShare {
    private Long id;
    private Long customerId;
    private Long userId;
    private CustomerSharePermission permission;
    private Long sharedBy;
    private LocalDateTime createdAt;
}
