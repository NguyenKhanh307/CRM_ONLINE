package vn.com.be_crm.application.quotation.mapper;

import vn.com.be_crm.application.quotation.dto.*;
import vn.com.be_crm.core.util.LineItemTotals;
import vn.com.be_crm.domain.quotation.entity.QuotationItem;
import vn.com.be_crm.domain.quotation.enums.QuotationLineStatus;

import java.math.BigDecimal;

// chuyển đổi Command <-> QuotationItem <-> QuotationItemResult. "amount" không lưu DB — tính
// bằng LineItemTotals ngay khi build Result.
public class QuotationItemCommandMapper {

    public static QuotationItem toEntity(CreateQuotationItemCommand cmd) {
        return QuotationItem.builder()
                .quotationId(cmd.getQuotationId()).productId(cmd.getProductId())
                .unit(cmd.getUnit())
                .quantity(cmd.getQuantity() != null ? cmd.getQuantity() : BigDecimal.ONE)
                .unitPrice(cmd.getUnitPrice() != null ? cmd.getUnitPrice() : BigDecimal.ZERO)
                .discount(cmd.getDiscount() != null ? cmd.getDiscount() : BigDecimal.ZERO)
                .taxRate(cmd.getTaxRate() != null ? cmd.getTaxRate() : BigDecimal.ZERO)
                .lineStatus(QuotationLineStatus.pending)
                .note(cmd.getNote()).build();
    }

    public static QuotationItem toEntity(UpdateQuotationItemCommand cmd, QuotationItem e) {
        return QuotationItem.builder()
                .id(e.getId()).quotationId(e.getQuotationId())
                .productId(cmd.getProductId() != null ? cmd.getProductId() : e.getProductId())
                .unit(cmd.getUnit() != null ? cmd.getUnit() : e.getUnit())
                .quantity(cmd.getQuantity() != null ? cmd.getQuantity() : e.getQuantity())
                .unitPrice(cmd.getUnitPrice() != null ? cmd.getUnitPrice() : e.getUnitPrice())
                .discount(cmd.getDiscount() != null ? cmd.getDiscount() : e.getDiscount())
                .taxRate(cmd.getTaxRate() != null ? cmd.getTaxRate() : e.getTaxRate())
                .lineStatus(cmd.getLineStatus() != null ? cmd.getLineStatus() : e.getLineStatus())
                .note(cmd.getNote() != null ? cmd.getNote() : e.getNote()).build();
    }

    public static QuotationItemResult toResult(QuotationItem e) {
        return QuotationItemResult.builder()
                .id(e.getId()).quotationId(e.getQuotationId()).productId(e.getProductId())
                .unit(e.getUnit())
                .quantity(e.getQuantity()).unitPrice(e.getUnitPrice()).discount(e.getDiscount())
                .taxRate(e.getTaxRate())
                .amount(LineItemTotals.lineAmount(e.getQuantity(), e.getUnitPrice(), e.getDiscount(), e.getTaxRate()))
                .lineStatus(e.getLineStatus()).note(e.getNote()).build();
    }

    private QuotationItemCommandMapper() {}
}
