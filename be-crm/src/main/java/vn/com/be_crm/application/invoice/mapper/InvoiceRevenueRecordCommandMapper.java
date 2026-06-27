package vn.com.be_crm.application.invoice.mapper;

import vn.com.be_crm.application.invoice.dto.*;
import vn.com.be_crm.domain.invoice.entity.InvoiceRevenueRecord;

import java.math.BigDecimal;

/** Chuyển đổi Command ↔ InvoiceRevenueRecord ↔ InvoiceRevenueRecordResult. */
public class InvoiceRevenueRecordCommandMapper {

    /**
     * Tạo InvoiceRevenueRecord từ CreateInvoiceRevenueRecordCommand.
     * @param cmd command tạo mới @return domain entity
     */
    public static InvoiceRevenueRecord toEntity(CreateInvoiceRevenueRecordCommand cmd) {
        return InvoiceRevenueRecord.builder()
                .invoiceId(cmd.getInvoiceId()).userId(cmd.getUserId())
                .revenueAmount(cmd.getRevenueAmount() != null ? cmd.getRevenueAmount() : BigDecimal.ZERO)
                .percentage(cmd.getPercentage() != null ? cmd.getPercentage() : BigDecimal.ZERO)
                .note(cmd.getNote()).build();
    }

    /**
     * Cập nhật InvoiceRevenueRecord từ UpdateInvoiceRevenueRecordCommand.
     * @param cmd command cập nhật @param e entity hiện tại @return domain entity đã cập nhật
     */
    public static InvoiceRevenueRecord toEntity(UpdateInvoiceRevenueRecordCommand cmd, InvoiceRevenueRecord e) {
        return InvoiceRevenueRecord.builder()
                .id(e.getId()).invoiceId(e.getInvoiceId()).userId(e.getUserId())
                .revenueAmount(cmd.getRevenueAmount() != null ? cmd.getRevenueAmount() : e.getRevenueAmount())
                .percentage(cmd.getPercentage() != null ? cmd.getPercentage() : e.getPercentage())
                .note(cmd.getNote() != null ? cmd.getNote() : e.getNote())
                .createdAt(e.getCreatedAt()).build();
    }

    /**
     * Chuyển InvoiceRevenueRecord sang InvoiceRevenueRecordResult.
     * @param e domain entity @return result DTO
     */
    public static InvoiceRevenueRecordResult toResult(InvoiceRevenueRecord e) {
        return InvoiceRevenueRecordResult.builder()
                .id(e.getId()).invoiceId(e.getInvoiceId()).userId(e.getUserId())
                .revenueAmount(e.getRevenueAmount()).percentage(e.getPercentage())
                .note(e.getNote()).createdAt(e.getCreatedAt()).build();
    }

    private InvoiceRevenueRecordCommandMapper() {}
}
