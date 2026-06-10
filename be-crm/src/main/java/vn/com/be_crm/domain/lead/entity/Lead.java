package vn.com.be_crm.domain.lead.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.lead.enums.LeadStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Domain entity đại diện cho tiềm năng bán hàng.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Lead {
    private Long id;
    private String code;
    private String name;
    private Long ownerId;
    private Long customerId;
    private Long contactId;
    private String source;
    private LeadStatus status;
    private BigDecimal estimatedValue;
    private String phone;
    private String email;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    /** Thời điểm xóa mềm. */
    private LocalDateTime deletedAt;
    /** ID người dùng đã xóa. */
    private Long deletedBy;
    /** True nếu đã ẩn khỏi thùng rác. */
    private boolean isPurged;
}
