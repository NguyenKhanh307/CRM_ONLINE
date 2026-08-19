package vn.com.be_crm.application.opportunity.mapper;

import vn.com.be_crm.application.opportunity.dto.*;
import vn.com.be_crm.core.util.LineItemTotals;
import vn.com.be_crm.domain.opportunity.entity.OpportunityItem;

import java.math.BigDecimal;

/** Chuyển đổi Command ↔ OpportunityItem ↔ OpportunityItemResult. */
public class OpportunityItemCommandMapper {

    /**
     * Tạo OpportunityItem từ CreateOpportunityItemCommand.
     * @param cmd command tạo mới @return domain entity
     */
    public static OpportunityItem toEntity(CreateOpportunityItemCommand cmd) {
        BigDecimal quantity = cmd.getQuantity() != null ? cmd.getQuantity() : BigDecimal.ONE;
        BigDecimal unitPrice = cmd.getUnitPrice() != null ? cmd.getUnitPrice() : BigDecimal.ZERO;
        BigDecimal discount = cmd.getDiscount() != null ? cmd.getDiscount() : BigDecimal.ZERO;
        // BE tự tính thành tiền dòng hàng (không có thuế) — không nhận amount thủ công từ FE
        return OpportunityItem.builder()
                .opportunityId(cmd.getOpportunityId()).productId(cmd.getProductId())
                .quantity(quantity).unitPrice(unitPrice).discount(discount)
                .amount(LineItemTotals.lineNet(quantity, unitPrice, discount))
                .note(cmd.getNote()).build();
    }

    /**
     * Cập nhật OpportunityItem từ UpdateOpportunityItemCommand.
     * @param cmd command cập nhật @param e entity hiện tại @return domain entity đã cập nhật
     */
    public static OpportunityItem toEntity(UpdateOpportunityItemCommand cmd, OpportunityItem e) {
        BigDecimal quantity = cmd.getQuantity() != null ? cmd.getQuantity() : e.getQuantity();
        BigDecimal unitPrice = cmd.getUnitPrice() != null ? cmd.getUnitPrice() : e.getUnitPrice();
        BigDecimal discount = cmd.getDiscount() != null ? cmd.getDiscount() : e.getDiscount();
        // BE tự tính thành tiền dòng hàng (không có thuế) — không nhận amount thủ công từ FE
        return OpportunityItem.builder()
                .id(e.getId()).opportunityId(e.getOpportunityId())
                .productId(cmd.getProductId() != null ? cmd.getProductId() : e.getProductId())
                .quantity(quantity).unitPrice(unitPrice).discount(discount)
                .amount(LineItemTotals.lineNet(quantity, unitPrice, discount))
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
