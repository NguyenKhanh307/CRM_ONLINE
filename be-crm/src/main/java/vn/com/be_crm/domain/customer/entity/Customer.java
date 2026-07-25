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
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    /** ID khách hàng. */
    private Long id;
    /** Mã khách hàng. */
    private String code;
    /** Tên khách hàng. */
    private String name;
    /** Tên viết tắt. */
    private String shortName;
    /** Loại khách hàng (cá nhân/doanh nghiệp). */
    private CustomerType type;
    /** Mã số thuế. */
    private String taxCode;
    /** Số điện thoại. */
    private String phone;
    /** Email. */
    private String email;
    /** Website. */
    private String website;
    /** Địa chỉ. */
    private String address;
    /** Ngành nghề. */
    private String industry;
    /** Nguồn khách hàng. */
    private String source;
    /** Trạng thái khách hàng. */
    private CustomerStatus status;
    /** Số ngày được nợ. */
    private Integer creditDays;
    /** Hạn mức nợ tối đa. */
    private BigDecimal creditLimit;
    /** Số tài khoản ngân hàng. */
    private String bankAccount;
    /** Tên ngân hàng. */
    private String bankName;
    /** Xếp hạng khách hàng (A/B/C). */
    private String rating;
    /** Doanh thu hàng năm. */
    private BigDecimal annualRevenue;
    /** Quy mô nhân sự. */
    private String employeeSize;
    /** Là nhà phân phối. */
    private boolean isDistributor;
    /** ID nhân viên phụ trách. */
    private Long ownerId;
    /** ID người tạo bản ghi (BE tự đóng dấu, client không gửi lên). */
    private Long createdBy;
    /** ID người sửa bản ghi gần nhất (BE tự đóng dấu). */
    private Long updatedBy;
    /** Thời điểm tạo. */
    private LocalDateTime createdAt;
    /** Thời điểm cập nhật gần nhất. */
    private LocalDateTime updatedAt;
    /** Thời điểm xóa mềm. */
    private LocalDateTime deletedAt;
    /** ID người dùng đã xóa. */
    private Long deletedBy;
    /** True nếu đã ẩn khỏi thùng rác. */
    private boolean isPurged;
}
