package vn.com.be_crm.domain.contact.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.contact.enums.ContactGender;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Domain entity đại diện cho liên hệ khách hàng.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Contact {
    /** ID liên hệ. */
    private Long id;
    /** ID khách hàng. */
    private Long customerId;
    /** ID nhân viên được phân công. */
    private Long assignedUserId;
    /** Xưng hô (Anh/Chị/Ông/Bà...). */
    private String salutation;
    /** Họ và tên. */
    private String fullName;
    /** Chức danh. */
    private String title;
    /** Phòng ban. */
    private String department;
    /** Vị trí công tác. */
    private String position;
    /** Email. */
    private String email;
    /** Email cơ quan. */
    private String workEmail;
    /** Email cá nhân. */
    private String personalEmail;
    /** Số Zalo. */
    private String zalo;
    /** Nguồn gốc liên hệ. */
    private String source;
    /** Giới tính. */
    private ContactGender gender;
    /** Ngày sinh. */
    private LocalDate dateOfBirth;
    /** Địa chỉ. */
    private String address;
    /** Không gọi điện. */
    private boolean doNotCall;
    /** Không gửi email. */
    private boolean doNotEmail;
    /** Là liên hệ chính của khách hàng. */
    private Boolean isPrimary;
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
