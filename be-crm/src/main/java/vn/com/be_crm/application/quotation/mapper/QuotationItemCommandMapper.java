package vn.com.be_crm.application.quotation.mapper;

import vn.com.be_crm.application.quotation.dto.*;
import vn.com.be_crm.domain.quotation.entity.QuotationItem;

import java.math.BigDecimal;

/** Chuyển đổi Command ↔ QuotationItem ↔ QuotationItemResult. */
public class QuotationItemCommandMapper {

    /**
     * Tạo QuotationItem từ CreateQuotationItemCommand.
     * @param cmd command tạo mới @return domain entity
     */
    public static QuotationItem toEntity(CreateQuotationItemCommand cmd) {
        return QuotationItem.builder()
                .quotationId(cmd.getQuotationId()).productId(cmd.getProductId())
                .unit(cmd.getUnit())
                .quantity(cmd.getQuantity() != null ? cmd.getQuantity() : BigDecimal.ONE)
                .unitPrice(cmd.getUnitPrice() != null ? cmd.getUnitPrice() : BigDecimal.ZERO)
                .discount(cmd.getDiscount() != null ? cmd.getDiscount() : BigDecimal.ZERO)
                .taxRate(cmd.getTaxRate() != null ? cmd.getTaxRate() : BigDecimal.ZERO)
                .amount(cmd.getAmount() != null ? cmd.getAmount() : BigDecimal.ZERO)
                .note(cmd.getNote()).build();
    }

    /**
     * Cập nhật QuotationItem từ UpdateQuotationItemCommand.
     * @param cmd command cập nhật @param e entity hiện tại @return domain entity đã cập nhật
     */
    public static QuotationItem toEntity(UpdateQuotationItemCommand cmd, QuotationItem e) {
        return QuotationItem.builder()
                .id(e.getId()).quotationId(e.getQuotationId())
                .productId(cmd.getProductId() != null ? cmd.getProductId() : e.getProductId())
                .unit(cmd.getUnit() != null ? cmd.getUnit() : e.getUnit())
                .quantity(cmd.getQuantity() != null ? cmd.getQuantity() : e.getQuantity())
                .unitPrice(cmd.getUnitPrice() != null ? cmd.getUnitPrice() : e.getUnitPrice())
                .discount(cmd.getDiscount() != null ? cmd.getDiscount() : e.getDiscount())
                .taxRate(cmd.getTaxRate() != null ? cmd.getTaxRate() : e.getTaxRate())
                .amount(cmd.getAmount() != null ? cmd.getAmount() : e.getAmount())
                .note(cmd.getNote() != null ? cmd.getNote() : e.getNote()).build();
    }

    /**
     * Chuyển QuotationItem sang QuotationItemResult.
     * @param e domain entity @return result DTO
     */
    public static QuotationItemResult toResult(QuotationItem e) {
        return QuotationItemResult.builder()
                .id(e.getId()).quotationId(e.getQuotationId()).productId(e.getProductId())
                .unit(e.getUnit())
                .quantity(e.getQuantity()).unitPrice(e.getUnitPrice()).discount(e.getDiscount())
                .taxRate(e.getTaxRate()).amount(e.getAmount()).note(e.getNote()).build();
    }

    private QuotationItemCommandMapper() {}
}
