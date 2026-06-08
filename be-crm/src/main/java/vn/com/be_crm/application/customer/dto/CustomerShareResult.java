package vn.com.be_crm.application.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.customer.enums.CustomerSharePermission;

import java.time.LocalDateTime;

/** Output DTO cho CustomerShare. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class CustomerShareResult {
    private Long id;
    private Long customerId;
    private Long userId;
    private CustomerSharePermission permission;
    private Long sharedBy;
    private LocalDateTime createdAt;
}
