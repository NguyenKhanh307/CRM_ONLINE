package vn.com.be_crm.application.opportunity.mapper;

import vn.com.be_crm.application.opportunity.dto.*;
import vn.com.be_crm.domain.opportunity.entity.Opportunity;
import vn.com.be_crm.domain.opportunity.enums.OpportunityStatus;

import java.math.BigDecimal;

/** Chuyển đổi Command ↔ Opportunity ↔ OpportunityResult. */
public class OpportunityCommandMapper {

    /**
     * Tạo Opportunity từ CreateOpportunityCommand.
     * Trạng thái KHÔNG nhận từ command — luôn suy ra từ giai đoạn pipeline.
     * @param cmd command tạo mới @param derivedStatus trạng thái suy ra từ stage @return domain entity
     */
    public static Opportunity toEntity(CreateOpportunityCommand cmd, OpportunityStatus derivedStatus) {
        return Opportunity.builder()
                .code(cmd.getCode()).name(cmd.getName()).opportunityType(cmd.getOpportunityType())
                .customerId(cmd.getCustomerId())
                .contactId(cmd.getContactId()).ownerId(cmd.getOwnerId()).stageId(cmd.getStageId())
                .pricePolicyId(cmd.getPricePolicyId())
                .amount(cmd.getAmount() != null ? cmd.getAmount() : BigDecimal.ZERO)
                .expectedRevenue(cmd.getExpectedRevenue())
                .probability(cmd.getProbability()).expectedCloseDate(cmd.getExpectedCloseDate())
                .source(cmd.getSource()).campaignId(cmd.getCampaignId())
                .winLossReason(cmd.getWinLossReason()).description(cmd.getDescription())
                .status(derivedStatus != null ? derivedStatus : OpportunityStatus.open).build();
    }

    /**
     * Cập nhật Opportunity từ UpdateOpportunityCommand.
     * Trạng thái KHÔNG nhận từ command — luôn suy ra từ giai đoạn pipeline hiện tại.
     * @param cmd command cập nhật @param e entity hiện tại
     * @param derivedStatus trạng thái suy ra từ stage @return domain entity đã cập nhật
     */
    public static Opportunity toEntity(UpdateOpportunityCommand cmd, Opportunity e, OpportunityStatus derivedStatus) {
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
                .expectedRevenue(cmd.getExpectedRevenue() != null ? cmd.getExpectedRevenue() : e.getExpectedRevenue())
                .probability(cmd.getProbability() != null ? cmd.getProbability() : e.getProbability())
                .expectedCloseDate(cmd.getExpectedCloseDate() != null ? cmd.getExpectedCloseDate() : e.getExpectedCloseDate())
                .source(cmd.getSource() != null ? cmd.getSource() : e.getSource())
                .campaignId(cmd.getCampaignId() != null ? cmd.getCampaignId() : e.getCampaignId())
                .winLossReason(cmd.getWinLossReason() != null ? cmd.getWinLossReason() : e.getWinLossReason())
                .description(cmd.getDescription() != null ? cmd.getDescription() : e.getDescription())
                .status(derivedStatus != null ? derivedStatus : e.getStatus())
                .createdBy(e.getCreatedBy()).updatedBy(e.getUpdatedBy())
                .createdAt(e.getCreatedAt()).build();
    }

    /**
     * Chuyển Opportunity sang OpportunityResult.
     * @param e domain entity @return result DTO
     */
    public static OpportunityResult toResult(Opportunity e) {
        return OpportunityResult.builder()
                .id(e.getId()).code(e.getCode()).name(e.getName()).opportunityType(e.getOpportunityType())
                .customerId(e.getCustomerId())
                .contactId(e.getContactId()).ownerId(e.getOwnerId()).stageId(e.getStageId())
                .pricePolicyId(e.getPricePolicyId())
                .amount(e.getAmount()).expectedRevenue(e.getExpectedRevenue()).probability(e.getProbability())
                .expectedCloseDate(e.getExpectedCloseDate())
                .source(e.getSource()).campaignId(e.getCampaignId()).winLossReason(e.getWinLossReason()).description(e.getDescription())
                .status(e.getStatus())
                .createdBy(e.getCreatedBy()).updatedBy(e.getUpdatedBy())
                .createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt()).build();
    }

    private OpportunityCommandMapper() {}
}
