package vn.com.be_crm.domain.lead.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Domain entity đại diện cho lịch sử chuyển giao tiềm năng.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeadTransfer {
    /** ID bản ghi chuyển giao. */
    private Long id;
    /** ID tiềm năng. */
    private Long leadId;
    /** ID người bàn giao (chuyển đi). */
    private Long fromUserId;
    /** ID người nhận bàn giao. */
    private Long toUserId;
    /** Lý do chuyển giao. */
    private String reason;
    /** Thời điểm chuyển giao. */
    private LocalDateTime transferredAt;
}
