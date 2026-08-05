package vn.com.be_crm.domain.quotation.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.quotation.enums.QuotationApprovalStatus;

import java.time.LocalDateTime;

// bước phê duyệt báo giá (chỉ 1 cấp)
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class QuotationApproval {
    private Long id;
    private Long quotationId;
    private Long approverId;
    private QuotationApprovalStatus status;
    private String comment;
    private LocalDateTime approvedAt;
    private LocalDateTime createdAt;
}
