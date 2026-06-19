package vn.com.be_crm.domain.customer.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.customer.enums.CustomerSharePermission;

import java.time.LocalDateTime;

/**
 * Domain entity đại diện cho việc chia sẻ khách hàng với người dùng.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerShare {
    /** ID bản ghi chia sẻ. */
    private Long id;
    /** ID khách hàng được chia sẻ. */
    private Long customerId;
    /** ID người dùng được chia sẻ. */
    private Long userId;
    /** Quyền được chia sẻ (xem/sửa...). */
    private CustomerSharePermission permission;
    /** ID người thực hiện chia sẻ. */
    private Long sharedBy;
    /** Thời điểm tạo. */
    private LocalDateTime createdAt;
}
