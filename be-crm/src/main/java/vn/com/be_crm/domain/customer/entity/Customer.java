package vn.com.be_crm.domain.customer.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.customer.enums.CustomerStatus;
import vn.com.be_crm.domain.customer.enums.CustomerType;

import java.math.BigDecimal;
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
    /** Tên viết tắt. */
    private String shortName;
    private CustomerType type;
    private String taxCode;
    private String phone;
    private String email;
    private String website;
    private String address;
    /** Ngành nghề. */
    private String industry;
    private String source;
    private CustomerStatus status;
    /** Số ngày được nợ. */
    private Integer creditDays;
    /** Hạn mức nợ tối đa. */
    private BigDecimal creditLimit;
    private String bankAccount;
    private String bankName;
    /** Xếp hạng khách hàng (A/B/C). */
    private String rating;
    /** Doanh thu hàng năm. */
    private BigDecimal annualRevenue;
    /** Quy mô nhân sự. */
    private String employeeSize;
    /** Là nhà phân phối. */
    private boolean isDistributor;
    private Long ownerId;
    private Long unitId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    /** Thời điểm xóa mềm. */
    private LocalDateTime deletedAt;
    /** ID người dùng đã xóa. */
    private Long deletedBy;
    /** True nếu đã ẩn khỏi thùng rác. */
    private boolean isPurged;
}
