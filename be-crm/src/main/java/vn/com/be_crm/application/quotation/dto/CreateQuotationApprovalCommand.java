package vn.com.be_crm.application.quotation.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.quotation.enums.QuotationApprovalStatus;

/** Input DTO khi tạo mới bước phê duyệt báo giá. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateQuotationApprovalCommand {
    private Long quotationId;
    private Long approverId;
    private Integer level;
    private QuotationApprovalStatus status;
    @Size(max = 500) private String comment;
}
