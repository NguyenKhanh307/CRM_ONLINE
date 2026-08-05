package vn.com.be_crm.application.opportunity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.be_crm.domain.opportunity.enums.OpportunityStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// output cho Opportunity
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class OpportunityResult {
    private Long id;
    private String code;
    private String name;
    private String opportunityType;
    private Long customerId;
    private Long contactId;
    private Long ownerId;
    private Long stageId;
    private Long pricePolicyId;
    private BigDecimal amount;
    private String source;
    private Long campaignId;
    private String winLossReason;
    private String description;
    private OpportunityStatus status;
    // Audit: BE tự đóng dấu (AuditInterceptor), client không gửi lên.
    private Long createdBy;
    private Long updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // Tên khóa ngoại — do BE resolve (INameResolver).
    private String customerName;
    private String contactName;
    private String ownerName;
    private String stageName;
    private String campaignName;
    // Tên người tạo/người sửa — do BE resolve (INameResolver).
    private String createdByName;
    private String updatedByName;
}
