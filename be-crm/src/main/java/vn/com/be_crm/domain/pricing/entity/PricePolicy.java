package vn.com.be_crm.domain.pricing.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.pricing.enums.PricePolicyStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Domain entity đại diện cho chính sách giá.
 */
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PricePolicy {
    /** ID chính sách giá. */
    private Long id;
    /** Mã chính sách. */
    private String code;
    /** Tên chính sách. */
    private String name;
    /** Loại chính sách. */
    private String type;
    /** Độ ưu tiên khi nhiều chính sách cùng áp dụng (số lớn ưu tiên hơn). */
    private Integer priority;
    /** Ngày bắt đầu hiệu lực. */
    private LocalDate startDate;
    /** Ngày kết thúc hiệu lực. */
    private LocalDate endDate;
    /** Trạng thái chính sách. */
    private PricePolicyStatus status;
    /** ID người tạo. */
    private Long createdBy;
    /** Thời điểm tạo. */
    private LocalDateTime createdAt;
    /** Thời điểm cập nhật gần nhất. */
    private LocalDateTime updatedAt;
}
