package vn.com.be_crm.application.invoice.mapper;

import vn.com.be_crm.application.invoice.dto.*;
import vn.com.be_crm.domain.invoice.entity.InvoicePaymentSchedule;
import vn.com.be_crm.domain.invoice.enums.PaymentScheduleStatus;

import java.math.BigDecimal;

/** Chuyển đổi Command ↔ InvoicePaymentSchedule ↔ InvoicePaymentScheduleResult. */
public class InvoicePaymentScheduleCommandMapper {

    /**
     * Tạo InvoicePaymentSchedule từ CreateInvoicePaymentScheduleCommand.
     * @param cmd command tạo mới @return domain entity
     */
    public static InvoicePaymentSchedule toEntity(CreateInvoicePaymentScheduleCommand cmd) {
        return InvoicePaymentSchedule.builder()
                .invoiceId(cmd.getInvoiceId())
                .installmentNo(cmd.getInstallmentNo() != null ? cmd.getInstallmentNo() : 1)
                .dueDate(cmd.getDueDate())
                .amount(cmd.getAmount() != null ? cmd.getAmount() : BigDecimal.ZERO)
                .paidAmount(cmd.getPaidAmount() != null ? cmd.getPaidAmount() : BigDecimal.ZERO)
                .status(cmd.getStatus() != null ? cmd.getStatus() : PaymentScheduleStatus.pending)
                .paidAt(cmd.getPaidAt()).note(cmd.getNote()).build();
    }

    /**
     * Cập nhật InvoicePaymentSchedule từ UpdateInvoicePaymentScheduleCommand.
     * @param cmd command cập nhật @param e entity hiện tại @return domain entity đã cập nhật
     */
    public static InvoicePaymentSchedule toEntity(UpdateInvoicePaymentScheduleCommand cmd, InvoicePaymentSchedule e) {
        return InvoicePaymentSchedule.builder()
                .id(e.getId()).invoiceId(e.getInvoiceId()).installmentNo(e.getInstallmentNo())
                .dueDate(cmd.getDueDate() != null ? cmd.getDueDate() : e.getDueDate())
                .amount(cmd.getAmount() != null ? cmd.getAmount() : e.getAmount())
                .paidAmount(cmd.getPaidAmount() != null ? cmd.getPaidAmount() : e.getPaidAmount())
                .status(cmd.getStatus() != null ? cmd.getStatus() : e.getStatus())
                .paidAt(cmd.getPaidAt() != null ? cmd.getPaidAt() : e.getPaidAt())
                .note(cmd.getNote() != null ? cmd.getNote() : e.getNote()).build();
    }

    /**
     * Chuyển InvoicePaymentSchedule sang InvoicePaymentScheduleResult.
     * @param e domain entity @return result DTO
     */
    public static InvoicePaymentScheduleResult toResult(InvoicePaymentSchedule e) {
        return InvoicePaymentScheduleResult.builder()
                .id(e.getId()).invoiceId(e.getInvoiceId()).installmentNo(e.getInstallmentNo())
                .dueDate(e.getDueDate()).amount(e.getAmount()).paidAmount(e.getPaidAmount())
                .status(e.getStatus()).paidAt(e.getPaidAt()).note(e.getNote()).build();
    }

    private InvoicePaymentScheduleCommandMapper() {}
}
