package vn.com.be_crm.domain.opportunity.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.opportunity.enums.OpportunityStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// cơ hội bán hàng
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Opportunity {
    private Long id;
    private String code;
    private String name;
    private String opportunityType;
    private Long customerId;
    private Long contactId;
    private Long ownerId;
    private Long stageId;
    private Long pricePolicyId;
    // giá trị cơ hội, cộng dồn từ dòng hàng
    private BigDecimal amount;
    private String source;
    // chiến dịch nguồn (attribution)
    private Long campaignId;
    private String winLossReason;
    private String description;
    private OpportunityStatus status;
    // createdBy/updatedBy do BE tự đóng dấu (audit), client không gửi lên
    private Long createdBy;
    private Long updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private Long deletedBy;
    private boolean isPurged;
}
