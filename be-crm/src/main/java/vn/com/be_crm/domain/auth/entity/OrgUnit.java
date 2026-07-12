package vn.com.be_crm.domain.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Domain entity đại diện cho đơn vị / phòng ban trong cơ cấu tổ chức.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrgUnit {

    /** ID đơn vị. */
    private Long id;

    /** Mã đơn vị, duy nhất. */
    private String code;

    /** Tên đơn vị. */
    private String name;

    /** ID đơn vị cha (null nếu là gốc). */
    private Long parentId;

    /** Trưởng đơn vị — người nhận thông báo thay cho cả đội (null nếu chưa gán). */
    private Long managerId;

    /** Cấp bậc trong cây tổ chức. */
    private Integer level;

    /** Đường dẫn cây, ví dụ: /1/4/9. */
    private String path;

    /** Thứ tự hiển thị. */
    private Integer sortOrder;

    /** Trạng thái hoạt động. */
    private Boolean isActive;

    /** Thời điểm tạo. */
    private LocalDateTime createdAt;

    /** Thời điểm cập nhật gần nhất. */
    private LocalDateTime updatedAt;
}
