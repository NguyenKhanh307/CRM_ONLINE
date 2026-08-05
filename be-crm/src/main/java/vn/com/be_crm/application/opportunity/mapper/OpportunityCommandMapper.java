package vn.com.be_crm.application.opportunity.mapper;

import vn.com.be_crm.application.opportunity.dto.*;
import vn.com.be_crm.domain.opportunity.entity.Opportunity;
import vn.com.be_crm.domain.opportunity.entity.OpportunityStage;
import vn.com.be_crm.domain.opportunity.enums.OpportunityStatus;

import java.math.BigDecimal;

// chuyển đổi Command <-> Opportunity <-> OpportunityResult.
// Mapper nhận thẳng OpportunityStage (thay vì trạng thái đã suy sẵn) để suy ra status từ giai
// đoạn pipeline — use case vốn đã phải tra giai đoạn nên không phát sinh truy vấn nào. Xác suất
// thắng (%) không còn lưu trên Opportunity nữa — đọc thẳng opportunity_stages.probability qua
// stage khi cần hiển thị.
public class OpportunityCommandMapper {

    // trạng thái và xác suất KHÔNG nhận từ command — luôn suy ra từ giai đoạn pipeline
    public static Opportunity toEntity(CreateOpportunityCommand cmd, OpportunityStage stage) {
        return Opportunity.builder()
                .code(cmd.getCode()).name(cmd.getName()).opportunityType(cmd.getOpportunityType())
                .customerId(cmd.getCustomerId())
                .contactId(cmd.getContactId()).ownerId(cmd.getOwnerId()).stageId(cmd.getStageId())
                .pricePolicyId(cmd.getPricePolicyId())
                .amount(cmd.getAmount() != null ? cmd.getAmount() : BigDecimal.ZERO)
                .source(cmd.getSource()).campaignId(cmd.getCampaignId())
                .winLossReason(cmd.getWinLossReason()).description(cmd.getDescription())
                .status(OpportunityStatus.fromStage(stage)).build();
    }

    // trạng thái KHÔNG nhận từ command — luôn suy ra từ giai đoạn pipeline hiện tại (kể cả khi
    // request không đổi giai đoạn: use case đã fallback về e.getStageId())
    public static Opportunity toEntity(UpdateOpportunityCommand cmd, Opportunity e, OpportunityStage stage) {
        return Opportunity.builder()
                .id(e.getId()).code(e.getCode())
                .name(cmd.getName() != null ? cmd.getName() : e.getName())
                .opportunityType(cmd.getOpportunityType() != null ? cmd.getOpportunityType() : e.getOpportunityType())
                .customerId(cmd.getCustomerId() != null ? cmd.getCustomerId() : e.getCustomerId())
                .contactId(cmd.getContactId() != null ? cmd.getContactId() : e.getContactId())
                .ownerId(cmd.getOwnerId() != null ? cmd.getOwnerId() : e.getOwnerId())
                .stageId(cmd.getStageId() != null ? cmd.getStageId() : e.getStageId())
                .pricePolicyId(cmd.getPricePolicyId() != null ? cmd.getPricePolicyId() : e.getPricePolicyId())
                .amount(cmd.getAmount() != null ? cmd.getAmount() : e.getAmount())
                .source(cmd.getSource() != null ? cmd.getSource() : e.getSource())
                .campaignId(cmd.getCampaignId() != null ? cmd.getCampaignId() : e.getCampaignId())
                .winLossReason(cmd.getWinLossReason() != null ? cmd.getWinLossReason() : e.getWinLossReason())
                .description(cmd.getDescription() != null ? cmd.getDescription() : e.getDescription())
                .status(OpportunityStatus.fromStage(stage))
                .createdBy(e.getCreatedBy()).updatedBy(e.getUpdatedBy())
                .createdAt(e.getCreatedAt()).build();
    }

    public static OpportunityResult toResult(Opportunity e) {
        return OpportunityResult.builder()
                .id(e.getId()).code(e.getCode()).name(e.getName()).opportunityType(e.getOpportunityType())
                .customerId(e.getCustomerId())
                .contactId(e.getContactId()).ownerId(e.getOwnerId()).stageId(e.getStageId())
                .pricePolicyId(e.getPricePolicyId())
                .amount(e.getAmount())
                .source(e.getSource()).campaignId(e.getCampaignId()).winLossReason(e.getWinLossReason()).description(e.getDescription())
                .status(e.getStatus())
                .createdBy(e.getCreatedBy()).updatedBy(e.getUpdatedBy())
                .createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt()).build();
    }

    private OpportunityCommandMapper() {}
}
