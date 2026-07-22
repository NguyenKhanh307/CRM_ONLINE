package vn.com.be_crm.application.opportunity.mapper;

import vn.com.be_crm.application.opportunity.dto.*;
import vn.com.be_crm.domain.opportunity.entity.Opportunity;
import vn.com.be_crm.domain.opportunity.entity.OpportunityStage;
import vn.com.be_crm.domain.opportunity.enums.OpportunityStatus;

import java.math.BigDecimal;

/**
 * Chuyển đổi Command ↔ Opportunity ↔ OpportunityResult.
 *
 * <p>Mapper nhận thẳng {@link OpportunityStage} (thay vì trạng thái đã suy sẵn) để suy ra
 * <b>cả hai</b> trường phụ thuộc giai đoạn — {@code status} và {@code probability} — từ một
 * nguồn duy nhất. Use case vốn đã phải tra giai đoạn nên không phát sinh truy vấn nào.</p>
 */
public class OpportunityCommandMapper {

    /**
     * Xác suất thắng suy ra từ giai đoạn pipeline ({@code opportunity_stages.probability}).
     * Chưa gắn giai đoạn → null (không bịa số).
     * @param stage giai đoạn hiện tại (có thể null) @return xác suất (%) hoặc null
     */
    private static BigDecimal probabilityOf(OpportunityStage stage) {
        return stage != null ? stage.getProbability() : null;
    }

    /**
     * Tạo Opportunity từ CreateOpportunityCommand.
     * Trạng thái và xác suất KHÔNG nhận từ command — luôn suy ra từ giai đoạn pipeline.
     * @param cmd command tạo mới @param stage giai đoạn được chọn (có thể null) @return domain entity
     */
    public static Opportunity toEntity(CreateOpportunityCommand cmd, OpportunityStage stage) {
        return Opportunity.builder()
                .code(cmd.getCode()).name(cmd.getName()).opportunityType(cmd.getOpportunityType())
                .customerId(cmd.getCustomerId())
                .contactId(cmd.getContactId()).ownerId(cmd.getOwnerId()).stageId(cmd.getStageId())
                .pricePolicyId(cmd.getPricePolicyId())
                .amount(cmd.getAmount() != null ? cmd.getAmount() : BigDecimal.ZERO)
                .expectedRevenue(cmd.getExpectedRevenue())
                .probability(probabilityOf(stage)).expectedCloseDate(cmd.getExpectedCloseDate())
                .source(cmd.getSource()).campaignId(cmd.getCampaignId())
                .winLossReason(cmd.getWinLossReason()).description(cmd.getDescription())
                .status(OpportunityStatus.fromStage(stage)).build();
    }

    /**
     * Cập nhật Opportunity từ UpdateOpportunityCommand.
     * Trạng thái và xác suất KHÔNG nhận từ command — luôn suy ra từ giai đoạn pipeline hiện tại
     * (kể cả khi request không đổi giai đoạn: use case đã fallback về {@code e.getStageId()}).
     * @param cmd command cập nhật @param e entity hiện tại
     * @param stage giai đoạn sau cập nhật (có thể null) @return domain entity đã cập nhật
     */
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
                .expectedRevenue(cmd.getExpectedRevenue() != null ? cmd.getExpectedRevenue() : e.getExpectedRevenue())
                .probability(probabilityOf(stage))
                .expectedCloseDate(cmd.getExpectedCloseDate() != null ? cmd.getExpectedCloseDate() : e.getExpectedCloseDate())
                .source(cmd.getSource() != null ? cmd.getSource() : e.getSource())
                .campaignId(cmd.getCampaignId() != null ? cmd.getCampaignId() : e.getCampaignId())
                .winLossReason(cmd.getWinLossReason() != null ? cmd.getWinLossReason() : e.getWinLossReason())
                .description(cmd.getDescription() != null ? cmd.getDescription() : e.getDescription())
                .status(OpportunityStatus.fromStage(stage))
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
