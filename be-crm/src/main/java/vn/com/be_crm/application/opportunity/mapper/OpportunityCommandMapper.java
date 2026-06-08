package vn.com.be_crm.application.opportunity.mapper;

import vn.com.be_crm.application.opportunity.dto.*;
import vn.com.be_crm.domain.opportunity.entity.Opportunity;
import vn.com.be_crm.domain.opportunity.enums.OpportunityStatus;

import java.math.BigDecimal;

/** Chuyển đổi Command ↔ Opportunity ↔ OpportunityResult. */
public class OpportunityCommandMapper {

    /**
     * Tạo Opportunity từ CreateOpportunityCommand.
     * @param cmd command tạo mới @return domain entity
     */
    public static Opportunity toEntity(CreateOpportunityCommand cmd) {
        return Opportunity.builder()
                .code(cmd.getCode()).name(cmd.getName()).customerId(cmd.getCustomerId())
                .contactId(cmd.getContactId()).ownerId(cmd.getOwnerId()).stageId(cmd.getStageId())
                .amount(cmd.getAmount() != null ? cmd.getAmount() : BigDecimal.ZERO)
                .probability(cmd.getProbability()).expectedCloseDate(cmd.getExpectedCloseDate())
                .status(cmd.getStatus() != null ? cmd.getStatus() : OpportunityStatus.open).build();
    }

    /**
     * Cập nhật Opportunity từ UpdateOpportunityCommand.
     * @param cmd command cập nhật @param e entity hiện tại @return domain entity đã cập nhật
     */
    public static Opportunity toEntity(UpdateOpportunityCommand cmd, Opportunity e) {
        return Opportunity.builder()
                .id(e.getId()).code(e.getCode())
                .name(cmd.getName() != null ? cmd.getName() : e.getName())
                .customerId(cmd.getCustomerId() != null ? cmd.getCustomerId() : e.getCustomerId())
                .contactId(cmd.getContactId() != null ? cmd.getContactId() : e.getContactId())
                .ownerId(cmd.getOwnerId() != null ? cmd.getOwnerId() : e.getOwnerId())
                .stageId(cmd.getStageId() != null ? cmd.getStageId() : e.getStageId())
                .amount(cmd.getAmount() != null ? cmd.getAmount() : e.getAmount())
                .probability(cmd.getProbability() != null ? cmd.getProbability() : e.getProbability())
                .expectedCloseDate(cmd.getExpectedCloseDate() != null ? cmd.getExpectedCloseDate() : e.getExpectedCloseDate())
                .status(cmd.getStatus() != null ? cmd.getStatus() : e.getStatus())
                .createdAt(e.getCreatedAt()).build();
    }

    /**
     * Chuyển Opportunity sang OpportunityResult.
     * @param e domain entity @return result DTO
     */
    public static OpportunityResult toResult(Opportunity e) {
        return OpportunityResult.builder()
                .id(e.getId()).code(e.getCode()).name(e.getName()).customerId(e.getCustomerId())
                .contactId(e.getContactId()).ownerId(e.getOwnerId()).stageId(e.getStageId())
                .amount(e.getAmount()).probability(e.getProbability())
                .expectedCloseDate(e.getExpectedCloseDate()).status(e.getStatus())
                .createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt()).build();
    }

    private OpportunityCommandMapper() {}
}
