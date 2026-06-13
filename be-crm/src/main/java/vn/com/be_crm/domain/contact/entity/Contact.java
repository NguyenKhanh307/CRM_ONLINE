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
    private Long id;
    private Long customerId;
    private Long assignedUserId;
    /** Xưng hô (Anh/Chị/Ông/Bà...). */
    private String salutation;
    private String fullName;
    /** Chức danh. */
    private String title;
    /** Phòng ban. */
    private String department;
    private String position;
    private String email;
    /** Email cơ quan. */
    private String workEmail;
    /** Email cá nhân. */
    private String personalEmail;
    private String zalo;
    /** Nguồn gốc liên hệ. */
    private String source;
    private ContactGender gender;
    private LocalDate dateOfBirth;
    private String address;
    /** Không gọi điện. */
    private boolean doNotCall;
    /** Không gửi email. */
    private boolean doNotEmail;
    private Boolean isPrimary;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    /** Thời điểm xóa mềm. */
    private LocalDateTime deletedAt;
    /** ID người dùng đã xóa. */
    private Long deletedBy;
    /** True nếu đã ẩn khỏi thùng rác. */
    private boolean isPurged;
}
