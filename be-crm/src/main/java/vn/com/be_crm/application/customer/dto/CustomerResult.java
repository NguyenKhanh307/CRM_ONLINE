package vn.com.be_crm.application.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.be_crm.domain.customer.enums.CustomerStatus;
import vn.com.be_crm.domain.customer.enums.CustomerType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Output DTO cho Customer. */
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class CustomerResult {
    private Long id;
    private String code;
    private String name;
    private String shortName;
    private CustomerType type;
    private String taxCode;
    private String phone;
    private String email;
    private String website;
    private String address;
    private String industry;
    private String source;
    private CustomerStatus status;
    private Integer creditDays;
    private BigDecimal creditLimit;
    private String bankAccount;
    private String bankName;
    private String rating;
    private BigDecimal annualRevenue;
    private String employeeSize;
    private Boolean isDistributor;
    private Long ownerId;
    private Long unitId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // Tên khóa ngoại — do BE resolve (INameResolver).
    private String ownerName;
    private String unitName;
}
