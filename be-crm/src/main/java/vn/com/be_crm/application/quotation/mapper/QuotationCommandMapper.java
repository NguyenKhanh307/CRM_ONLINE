package vn.com.be_crm.application.quotation.mapper;

import vn.com.be_crm.application.quotation.dto.*;
import vn.com.be_crm.domain.quotation.entity.Quotation;
import vn.com.be_crm.domain.quotation.enums.QuotationStatus;

import java.math.BigDecimal;

/** Chuyển đổi Command ↔ Quotation ↔ QuotationResult. */
public class QuotationCommandMapper {

    /**
     * Tạo Quotation từ CreateQuotationCommand.
     * @param cmd command tạo mới @return domain entity
     */
    public static Quotation toEntity(CreateQuotationCommand cmd) {
        return Quotation.builder()
                .code(cmd.getCode()).customerId(cmd.getCustomerId()).contactId(cmd.getContactId())
                .ownerId(cmd.getOwnerId()).quoteDate(cmd.getQuoteDate()).validUntil(cmd.getValidUntil())
                .status(cmd.getStatus() != null ? cmd.getStatus() : QuotationStatus.draft)
                .subtotal(cmd.getSubtotal() != null ? cmd.getSubtotal() : BigDecimal.ZERO)
                .discount(cmd.getDiscount() != null ? cmd.getDiscount() : BigDecimal.ZERO)
                .tax(cmd.getTax() != null ? cmd.getTax() : BigDecimal.ZERO)
                .total(cmd.getTotal() != null ? cmd.getTotal() : BigDecimal.ZERO)
                .note(cmd.getNote()).build();
    }

    /**
     * Cập nhật Quotation từ UpdateQuotationCommand.
     * @param cmd command cập nhật @param e entity hiện tại @return domain entity đã cập nhật
     */
    public static Quotation toEntity(UpdateQuotationCommand cmd, Quotation e) {
        return Quotation.builder()
                .id(e.getId()).code(e.getCode())
                .customerId(cmd.getCustomerId() != null ? cmd.getCustomerId() : e.getCustomerId())
                .contactId(cmd.getContactId() != null ? cmd.getContactId() : e.getContactId())
                .ownerId(cmd.getOwnerId() != null ? cmd.getOwnerId() : e.getOwnerId())
                .quoteDate(cmd.getQuoteDate() != null ? cmd.getQuoteDate() : e.getQuoteDate())
                .validUntil(cmd.getValidUntil() != null ? cmd.getValidUntil() : e.getValidUntil())
                .status(cmd.getStatus() != null ? cmd.getStatus() : e.getStatus())
                .subtotal(cmd.getSubtotal() != null ? cmd.getSubtotal() : e.getSubtotal())
                .discount(cmd.getDiscount() != null ? cmd.getDiscount() : e.getDiscount())
                .tax(cmd.getTax() != null ? cmd.getTax() : e.getTax())
                .total(cmd.getTotal() != null ? cmd.getTotal() : e.getTotal())
                .note(cmd.getNote() != null ? cmd.getNote() : e.getNote())
                .createdAt(e.getCreatedAt()).build();
    }

    /**
     * Chuyển Quotation sang QuotationResult.
     * @param e domain entity @return result DTO
     */
    public static QuotationResult toResult(Quotation e) {
        return QuotationResult.builder()
                .id(e.getId()).code(e.getCode()).customerId(e.getCustomerId()).contactId(e.getContactId())
                .ownerId(e.getOwnerId()).quoteDate(e.getQuoteDate()).validUntil(e.getValidUntil())
                .status(e.getStatus()).subtotal(e.getSubtotal()).discount(e.getDiscount())
                .tax(e.getTax()).total(e.getTotal()).note(e.getNote())
                .createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt()).build();
    }

    private QuotationCommandMapper() {}
}
