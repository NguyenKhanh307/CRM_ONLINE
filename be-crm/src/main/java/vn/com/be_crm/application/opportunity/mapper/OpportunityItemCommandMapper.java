package vn.com.be_crm.application.opportunity.mapper;

import vn.com.be_crm.application.opportunity.dto.*;
import vn.com.be_crm.domain.opportunity.entity.OpportunityItem;

import java.math.BigDecimal;

/** Chuyển đổi Command ↔ OpportunityItem ↔ OpportunityItemResult. */
public class OpportunityItemCommandMapper {

    /**
     * Tạo OpportunityItem từ CreateOpportunityItemCommand.
     * @param cmd command tạo mới @return domain entity
     */
    public static OpportunityItem toEntity(CreateOpportunityItemCommand cmd) {
        return OpportunityItem.builder()
                .opportunityId(cmd.getOpportunityId()).productId(cmd.getProductId())
                .quantity(cmd.getQuantity() != null ? cmd.getQuantity() : BigDecimal.ONE)
                .unitPrice(cmd.getUnitPrice() != null ? cmd.getUnitPrice() : BigDecimal.ZERO)
                .discount(cmd.getDiscount() != null ? cmd.getDiscount() : BigDecimal.ZERO)
                .amount(cmd.getAmount() != null ? cmd.getAmount() : BigDecimal.ZERO)
                .note(cmd.getNote()).build();
    }

    /**
     * Cập nhật OpportunityItem từ UpdateOpportunityItemCommand.
     * @param cmd command cập nhật @param e entity hiện tại @return domain entity đã cập nhật
     */
    public static OpportunityItem toEntity(UpdateOpportunityItemCommand cmd, OpportunityItem e) {
        return OpportunityItem.builder()
                .id(e.getId()).opportunityId(e.getOpportunityId())
                .productId(cmd.getProductId() != null ? cmd.getProductId() : e.getProductId())
                .quantity(cmd.getQuantity() != null ? cmd.getQuantity() : e.getQuantity())
                .unitPrice(cmd.getUnitPrice() != null ? cmd.getUnitPrice() : e.getUnitPrice())
                .discount(cmd.getDiscount() != null ? cmd.getDiscount() : e.getDiscount())
                .amount(cmd.getAmount() != null ? cmd.getAmount() : e.getAmount())
                .note(cmd.getNote() != null ? cmd.getNote() : e.getNote()).build();
    }

    /**
     * Chuyển OpportunityItem sang OpportunityItemResult.
     * @param e domain entity @return result DTO
     */
    public static OpportunityItemResult toResult(OpportunityItem e) {
        return OpportunityItemResult.builder()
                .id(e.getId()).opportunityId(e.getOpportunityId()).productId(e.getProductId())
                .quantity(e.getQuantity()).unitPrice(e.getUnitPrice()).discount(e.getDiscount())
                .amount(e.getAmount()).note(e.getNote()).build();
    }

    private OpportunityItemCommandMapper() {}
}
