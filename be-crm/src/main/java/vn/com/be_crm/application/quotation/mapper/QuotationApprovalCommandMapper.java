package vn.com.be_crm.application.quotation.mapper;

import vn.com.be_crm.application.quotation.dto.*;
import vn.com.be_crm.domain.quotation.entity.QuotationApproval;
import vn.com.be_crm.domain.quotation.enums.QuotationApprovalStatus;

/** Chuyển đổi Command ↔ QuotationApproval ↔ QuotationApprovalResult. */
public class QuotationApprovalCommandMapper {

    /**
     * Tạo QuotationApproval từ CreateQuotationApprovalCommand.
     * @param cmd command tạo mới @return domain entity
     */
    public static QuotationApproval toEntity(CreateQuotationApprovalCommand cmd) {
        return QuotationApproval.builder()
                .quotationId(cmd.getQuotationId()).approverId(cmd.getApproverId())
                .level(cmd.getLevel() != null ? cmd.getLevel() : 1)
                .status(cmd.getStatus() != null ? cmd.getStatus() : QuotationApprovalStatus.pending)
                .comment(cmd.getComment()).build();
    }

    /**
     * Cập nhật QuotationApproval từ UpdateQuotationApprovalCommand.
     * @param cmd command cập nhật @param e entity hiện tại @return domain entity đã cập nhật
     */
    public static QuotationApproval toEntity(UpdateQuotationApprovalCommand cmd, QuotationApproval e) {
        return QuotationApproval.builder()
                .id(e.getId()).quotationId(e.getQuotationId()).approverId(e.getApproverId()).level(e.getLevel())
                .status(cmd.getStatus() != null ? cmd.getStatus() : e.getStatus())
                .comment(cmd.getComment() != null ? cmd.getComment() : e.getComment())
                .approvedAt(cmd.getApprovedAt() != null ? cmd.getApprovedAt() : e.getApprovedAt())
                .createdAt(e.getCreatedAt()).build();
    }

    /**
     * Chuyển QuotationApproval sang QuotationApprovalResult.
     * @param e domain entity @return result DTO
     */
    public static QuotationApprovalResult toResult(QuotationApproval e) {
        return QuotationApprovalResult.builder()
                .id(e.getId()).quotationId(e.getQuotationId()).approverId(e.getApproverId())
                .level(e.getLevel()).status(e.getStatus()).comment(e.getComment())
                .approvedAt(e.getApprovedAt()).createdAt(e.getCreatedAt()).build();
    }

    private QuotationApprovalCommandMapper() {}
}
