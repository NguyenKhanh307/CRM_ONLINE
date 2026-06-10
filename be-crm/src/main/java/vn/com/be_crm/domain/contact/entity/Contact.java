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
    private String fullName;
    private String position;
    private String email;
    private ContactGender gender;
    private LocalDate dateOfBirth;
    private String address;
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
