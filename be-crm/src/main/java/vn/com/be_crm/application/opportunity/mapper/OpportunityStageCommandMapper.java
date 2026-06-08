package vn.com.be_crm.application.opportunity.mapper;

import vn.com.be_crm.application.opportunity.dto.*;
import vn.com.be_crm.domain.opportunity.entity.OpportunityStage;

import java.math.BigDecimal;

/** Chuyển đổi Command ↔ OpportunityStage ↔ OpportunityStageResult. */
public class OpportunityStageCommandMapper {

    /** Tạo từ command tạo mới. @param c command @return entity */
    public static OpportunityStage toEntity(CreateOpportunityStageCommand c) {
        return OpportunityStage.builder()
                .name(c.getName()).sortOrder(c.getSortOrder() != null ? c.getSortOrder() : 0)
                .probability(c.getProbability() != null ? c.getProbability() : BigDecimal.ZERO)
                .isWon(c.getIsWon() != null ? c.getIsWon() : false)
                .isLost(c.getIsLost() != null ? c.getIsLost() : false).build();
    }

    /** Cập nhật từ command. @param c command @param e existing @return entity */
    public static OpportunityStage toEntity(UpdateOpportunityStageCommand c, OpportunityStage e) {
        return OpportunityStage.builder().id(e.getId())
                .name(c.getName() != null ? c.getName() : e.getName())
                .sortOrder(c.getSortOrder() != null ? c.getSortOrder() : e.getSortOrder())
                .probability(c.getProbability() != null ? c.getProbability() : e.getProbability())
                .isWon(c.getIsWon() != null ? c.getIsWon() : e.getIsWon())
                .isLost(c.getIsLost() != null ? c.getIsLost() : e.getIsLost())
                .createdAt(e.getCreatedAt()).build();
    }

    /** Chuyển sang result DTO. @param e entity @return result */
    public static OpportunityStageResult toResult(OpportunityStage e) {
        return OpportunityStageResult.builder().id(e.getId()).name(e.getName())
                .sortOrder(e.getSortOrder()).probability(e.getProbability())
                .isWon(e.getIsWon()).isLost(e.getIsLost())
                .createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt()).build();
    }

    private OpportunityStageCommandMapper() {}
}
