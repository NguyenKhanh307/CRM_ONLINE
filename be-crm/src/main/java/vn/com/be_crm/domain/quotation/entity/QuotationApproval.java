package vn.com.be_crm.domain.quotation.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.quotation.enums.QuotationApprovalStatus;

import java.time.LocalDateTime;

/**
 * Domain entity đại diện cho bước phê duyệt báo giá.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuotationApproval {
    /** ID bước phê duyệt. */
    private Long id;
    /** ID báo giá. */
    private Long quotationId;
    /** ID người phê duyệt. */
    private Long approverId;
    /** Cấp phê duyệt (thứ tự). */
    private Integer level;
    /** Trạng thái phê duyệt. */
    private QuotationApprovalStatus status;
    /** Ý kiến phê duyệt. */
    private String comment;
    /** Thời điểm phê duyệt. */
    private LocalDateTime approvedAt;
    /** Thời điểm tạo. */
    private LocalDateTime createdAt;
}
