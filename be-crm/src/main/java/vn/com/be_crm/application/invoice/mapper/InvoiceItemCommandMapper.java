package vn.com.be_crm.application.invoice.mapper;

import vn.com.be_crm.application.invoice.dto.*;
import vn.com.be_crm.domain.invoice.entity.InvoiceItem;

import java.math.BigDecimal;

/** Chuyển đổi Command ↔ InvoiceItem ↔ InvoiceItemResult. */
public class InvoiceItemCommandMapper {

    /**
     * Tạo InvoiceItem từ CreateInvoiceItemCommand.
     * @param cmd command tạo mới @return domain entity
     */
    public static InvoiceItem toEntity(CreateInvoiceItemCommand cmd) {
        return InvoiceItem.builder()
                .invoiceId(cmd.getInvoiceId()).productId(cmd.getProductId())
                .unit(cmd.getUnit())
                .quantity(cmd.getQuantity() != null ? cmd.getQuantity() : BigDecimal.ONE)
                .unitPrice(cmd.getUnitPrice() != null ? cmd.getUnitPrice() : BigDecimal.ZERO)
                .discount(cmd.getDiscount() != null ? cmd.getDiscount() : BigDecimal.ZERO)
                .taxRate(cmd.getTaxRate() != null ? cmd.getTaxRate() : BigDecimal.ZERO)
                .amount(cmd.getAmount() != null ? cmd.getAmount() : BigDecimal.ZERO)
                .note(cmd.getNote()).build();
    }

    /**
     * Cập nhật InvoiceItem từ UpdateInvoiceItemCommand.
     * @param cmd command cập nhật @param e entity hiện tại @return domain entity đã cập nhật
     */
    public static InvoiceItem toEntity(UpdateInvoiceItemCommand cmd, InvoiceItem e) {
        return InvoiceItem.builder()
                .id(e.getId()).invoiceId(e.getInvoiceId())
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
     * Chuyển InvoiceItem sang InvoiceItemResult.
     * @param e domain entity @return result DTO
     */
    public static InvoiceItemResult toResult(InvoiceItem e) {
        return InvoiceItemResult.builder()
                .id(e.getId()).invoiceId(e.getInvoiceId()).productId(e.getProductId())
                .unit(e.getUnit())
                .quantity(e.getQuantity()).unitPrice(e.getUnitPrice()).discount(e.getDiscount())
                .taxRate(e.getTaxRate()).amount(e.getAmount()).note(e.getNote()).build();
    }

    private InvoiceItemCommandMapper() {}
}
