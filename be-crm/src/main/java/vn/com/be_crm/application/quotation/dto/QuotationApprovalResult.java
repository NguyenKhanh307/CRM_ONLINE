package vn.com.be_crm.application.quotation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.quotation.enums.QuotationApprovalStatus;

import java.time.LocalDateTime;

/** Output DTO cho QuotationApproval. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class QuotationApprovalResult {
    private Long id;
    private Long quotationId;
    private Long approverId;
    private Integer level;
    private QuotationApprovalStatus status;
    private String comment;
    private LocalDateTime approvedAt;
    private LocalDateTime createdAt;
}
