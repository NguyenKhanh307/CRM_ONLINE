package vn.com.be_crm.domain.customer.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.customer.enums.CustomerStatus;
import vn.com.be_crm.domain.customer.enums.CustomerType;

import java.time.LocalDateTime;

/**
 * Domain entity đại diện cho khách hàng trong hệ thống CRM.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    private Long id;
    private String code;
    private String name;
    private CustomerType type;
    private String taxCode;
    private String phone;
    private String email;
    private String address;
    private String source;
    private CustomerStatus status;
    private Long ownerId;
    private Long unitId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    /** Thời điểm xóa mềm. */
    private LocalDateTime deletedAt;
}
