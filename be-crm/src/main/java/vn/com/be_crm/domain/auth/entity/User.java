package vn.com.be_crm.domain.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.auth.enums.UserStatus;

import java.time.LocalDateTime;

/**
 * Domain entity đại diện cho người dùng hệ thống.
 */
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /** ID người dùng. */
    private Long id;

    /** Email đăng nhập, duy nhất. */
    private String email;

    /** Mật khẩu đã hash (bcrypt). */
    private String passwordHash;

    /** Họ và tên đầy đủ. */
    private String fullName;

    /** Số điện thoại. */
    private String phone;

    /** URL avatar. */
    private String avatarUrl;

    /** ID đơn vị trực thuộc. */
    private Long unitId;

    /** Trạng thái tài khoản. */
    private UserStatus status;

    /** Thời điểm đăng nhập gần nhất. */
    private LocalDateTime lastLoginAt;

    /** Thời điểm tạo. */
    private LocalDateTime createdAt;

    /** Thời điểm cập nhật gần nhất. */
    private LocalDateTime updatedAt;

    /** Thời điểm xóa mềm (null nếu chưa xóa). */
    private LocalDateTime deletedAt;

    /** Token kích hoạt tài khoản (UUID). Null sau khi đã kích hoạt. */
    private String activationToken;

    /** Thời điểm hết hạn link kích hoạt (1 ngày sau khi admin đăng ký). */
    private LocalDateTime activationExpiresAt;

    /** Năm sớm nhất mà nhân viên được xem data (mặc định = năm kích hoạt tài khoản). Null = không giới hạn. */
    private Integer dataAccessFromYear;
}
