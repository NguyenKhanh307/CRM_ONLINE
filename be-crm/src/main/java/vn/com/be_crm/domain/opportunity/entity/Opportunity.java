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
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Opportunity {
    private Long id;
    private String code;
    private String name;
    private Long customerId;
    private Long contactId;
    private Long ownerId;
    private Long stageId;
    private BigDecimal amount;
    private BigDecimal probability;
    private LocalDate expectedCloseDate;
    private OpportunityStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    /** Thời điểm xóa mềm. */
    private LocalDateTime deletedAt;
    /** ID người dùng đã xóa. */
    private Long deletedBy;
    /** True nếu đã ẩn khỏi thùng rác. */
    private boolean isPurged;
}
