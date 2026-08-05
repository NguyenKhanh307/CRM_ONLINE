package vn.com.be_crm.application.quotation.mapper;

import vn.com.be_crm.application.quotation.dto.*;
import vn.com.be_crm.domain.quotation.entity.QuotationApproval;
import vn.com.be_crm.domain.quotation.enums.QuotationApprovalStatus;

// chuyển đổi Command <-> QuotationApproval <-> QuotationApprovalResult
public class QuotationApprovalCommandMapper {

    public static QuotationApproval toEntity(CreateQuotationApprovalCommand cmd) {
        return QuotationApproval.builder()
                .quotationId(cmd.getQuotationId()).approverId(cmd.getApproverId())
                .status(cmd.getStatus() != null ? cmd.getStatus() : QuotationApprovalStatus.pending)
                .comment(cmd.getComment()).build();
    }

    public static QuotationApproval toEntity(UpdateQuotationApprovalCommand cmd, QuotationApproval e) {
        return QuotationApproval.builder()
                .id(e.getId()).quotationId(e.getQuotationId()).approverId(e.getApproverId())
                .status(cmd.getStatus() != null ? cmd.getStatus() : e.getStatus())
                .comment(cmd.getComment() != null ? cmd.getComment() : e.getComment())
                .approvedAt(cmd.getApprovedAt() != null ? cmd.getApprovedAt() : e.getApprovedAt())
                .createdAt(e.getCreatedAt()).build();
    }

    public static QuotationApprovalResult toResult(QuotationApproval e) {
        return QuotationApprovalResult.builder()
                .id(e.getId()).quotationId(e.getQuotationId()).approverId(e.getApproverId())
                .status(e.getStatus()).comment(e.getComment())
                .approvedAt(e.getApprovedAt()).createdAt(e.getCreatedAt()).build();
    }

    private QuotationApprovalCommandMapper() {}
}
