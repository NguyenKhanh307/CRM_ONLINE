package vn.com.be_crm.domain.warehouse.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** Domain entity đại diện cho kho hàng. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class Warehouse {
    /** ID kho. */
    private Long id;
    /** Mã kho. */
    private String code;
    /** Tên kho. */
    private String name;
    /** Địa chỉ kho. */
    private String address;
    /** Đang hoạt động. */
    private Boolean isActive;
    /** Thời điểm tạo. */
    private LocalDateTime createdAt;
    /** Thời điểm cập nhật gần nhất. */
    private LocalDateTime updatedAt;
}
