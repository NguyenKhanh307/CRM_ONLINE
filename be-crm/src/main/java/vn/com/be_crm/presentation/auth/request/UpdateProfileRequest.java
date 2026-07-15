package vn.com.be_crm.presentation.auth.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * HTTP request body cho PUT /api/auth/me — người dùng tự sửa hồ sơ của mình.
 * Chỉ cho phép các trường an toàn: họ tên, số điện thoại, ảnh đại diện.
 */
@Getter
@NoArgsConstructor
public class UpdateProfileRequest {

    /** Họ và tên đầy đủ. */
    @Size(max = 30, message = "Họ tên tối đa 30 ký tự")
    private String fullName;

    /** Số điện thoại. */
    @Size(max = 11, message = "Số điện thoại tối đa 11 ký tự")
    private String phone;

    /** URL ảnh đại diện (link Cloudinary hoặc ảnh Google). */
    private String avatarUrl;
}
