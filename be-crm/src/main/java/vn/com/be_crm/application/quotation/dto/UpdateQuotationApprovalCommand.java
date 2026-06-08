package vn.com.be_crm.application.quotation.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.quotation.enums.QuotationApprovalStatus;

import java.time.LocalDateTime;

/** Input DTO khi cập nhật bước phê duyệt báo giá. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class UpdateQuotationApprovalCommand {
    private Long id;
    private QuotationApprovalStatus status;
    @Size(max = 500) private String comment;
    private LocalDateTime approvedAt;
}
