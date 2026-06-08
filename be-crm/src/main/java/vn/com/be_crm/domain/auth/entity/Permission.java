package vn.com.be_crm.domain.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Domain entity đại diện cho quyền hạn trong hệ thống.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Permission {

    /** ID quyền. */
    private Long id;

    /** Mã quyền, duy nhất (vd: order.view, quotation.create). */
    private String code;

    /** Tên quyền hiển thị. */
    private String name;

    /** Module chứa quyền này (vd: order, quotation). */
    private String module;

    /** Mô tả ngắn. */
    private String description;

    /** Thời điểm tạo. */
    private LocalDateTime createdAt;

    /** Thời điểm cập nhật gần nhất. */
    private LocalDateTime updatedAt;
}
