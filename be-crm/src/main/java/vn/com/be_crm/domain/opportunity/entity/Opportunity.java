package vn.com.be_crm.domain.opportunity.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.opportunity.enums.OpportunityStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Domain entity đại diện cho cơ hội bán hàng.
 */
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Opportunity {
    /** ID cơ hội. */
    private Long id;
    /** Mã cơ hội. */
    private String code;
    /** Tên cơ hội. */
    private String name;
    /** Loại cơ hội (KH mới/cũ...). */
    private String opportunityType;
    /** ID khách hàng. */
    private Long customerId;
    /** ID liên hệ. */
    private Long contactId;
    /** ID người phụ trách. */
    private Long ownerId;
    /** ID giai đoạn pipeline hiện tại. */
    private Long stageId;
    /** ID chính sách giá áp dụng (pricebook). */
    private Long pricePolicyId;
    /** Giá trị cơ hội (cộng dồn từ dòng hàng). */
    private BigDecimal amount;
    /** Doanh số kỳ vọng. */
    private BigDecimal expectedRevenue;
    /** Xác suất thắng (%). */
    private BigDecimal probability;
    /** Ngày dự kiến chốt. */
    private LocalDate expectedCloseDate;
    /** Nguồn gốc cơ hội. */
    private String source;
    /** Lý do thắng/thua. */
    private String winLossReason;
    /** Mô tả. */
    private String description;
    /** Trạng thái cơ hội. */
    private OpportunityStatus status;
    /** Thời điểm tạo. */
    private LocalDateTime createdAt;
    /** Thời điểm cập nhật gần nhất. */
    private LocalDateTime updatedAt;
    /** Thời điểm xóa mềm. */
    private LocalDateTime deletedAt;
    /** ID người dùng đã xóa. */
    private Long deletedBy;
    /** True nếu đã ẩn khỏi thùng rác. */
    private boolean isPurged;
}
