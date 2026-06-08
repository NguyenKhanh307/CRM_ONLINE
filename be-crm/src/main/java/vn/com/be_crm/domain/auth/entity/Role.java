package vn.com.be_crm.domain.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Domain entity đại diện cho vai trò trong hệ thống phân quyền.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    /** ID vai trò. */
    private Long id;

    /** Mã vai trò, duy nhất (vd: ADMIN, SALES). */
    private String code;

    /** Tên vai trò hiển thị. */
    private String name;

    /** Mô tả ngắn. */
    private String description;

    /** true nếu là vai trò hệ thống — không cho phép xóa. */
    private Boolean isSystem;

    /** Thời điểm tạo. */
    private LocalDateTime createdAt;

    /** Thời điểm cập nhật gần nhất. */
    private LocalDateTime updatedAt;
}
