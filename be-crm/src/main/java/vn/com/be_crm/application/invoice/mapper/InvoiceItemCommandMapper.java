package vn.com.be_crm.application.invoice.mapper;

import vn.com.be_crm.application.invoice.dto.*;
import vn.com.be_crm.core.util.LineItemTotals;
import vn.com.be_crm.domain.invoice.entity.InvoiceItem;

import java.math.BigDecimal;

// chuyển đổi Command <-> InvoiceItem <-> InvoiceItemResult. "amount" không lưu DB — tính bằng
// LineItemTotals ngay khi build Result.
public class InvoiceItemCommandMapper {

    public static InvoiceItem toEntity(CreateInvoiceItemCommand cmd) {
        return InvoiceItem.builder()
                .invoiceId(cmd.getInvoiceId()).productId(cmd.getProductId())
                .unit(cmd.getUnit())
                .quantity(cmd.getQuantity() != null ? cmd.getQuantity() : BigDecimal.ONE)
                .unitPrice(cmd.getUnitPrice() != null ? cmd.getUnitPrice() : BigDecimal.ZERO)
                .discount(cmd.getDiscount() != null ? cmd.getDiscount() : BigDecimal.ZERO)
                .taxRate(cmd.getTaxRate() != null ? cmd.getTaxRate() : BigDecimal.ZERO)
                .note(cmd.getNote()).build();
    }

    public static InvoiceItem toEntity(UpdateInvoiceItemCommand cmd, InvoiceItem e) {
        return InvoiceItem.builder()
                .id(e.getId()).invoiceId(e.getInvoiceId())
                .productId(cmd.getProductId() != null ? cmd.getProductId() : e.getProductId())
                .unit(cmd.getUnit() != null ? cmd.getUnit() : e.getUnit())
                .quantity(cmd.getQuantity() != null ? cmd.getQuantity() : e.getQuantity())
                .unitPrice(cmd.getUnitPrice() != null ? cmd.getUnitPrice() : e.getUnitPrice())
                .discount(cmd.getDiscount() != null ? cmd.getDiscount() : e.getDiscount())
                .taxRate(cmd.getTaxRate() != null ? cmd.getTaxRate() : e.getTaxRate())
                .note(cmd.getNote() != null ? cmd.getNote() : e.getNote()).build();
    }

    public static InvoiceItemResult toResult(InvoiceItem e) {
        return InvoiceItemResult.builder()
                .id(e.getId()).invoiceId(e.getInvoiceId()).productId(e.getProductId())
                .unit(e.getUnit())
                .quantity(e.getQuantity()).unitPrice(e.getUnitPrice()).discount(e.getDiscount())
                .taxRate(e.getTaxRate())
                .amount(LineItemTotals.lineAmount(e.getQuantity(), e.getUnitPrice(), e.getDiscount(), e.getTaxRate()))
                .note(e.getNote()).build();
    }

    private InvoiceItemCommandMapper() {}
}
