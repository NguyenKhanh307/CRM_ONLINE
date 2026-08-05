package vn.com.be_crm.application.invoice.mapper;

import vn.com.be_crm.application.invoice.dto.*;
import vn.com.be_crm.domain.invoice.entity.InvoicePaymentSchedule;
import vn.com.be_crm.domain.invoice.enums.PaymentScheduleStatus;

import java.math.BigDecimal;

// chuyển đổi Command <-> InvoicePaymentSchedule <-> InvoicePaymentScheduleResult
public class InvoicePaymentScheduleCommandMapper {

    public static InvoicePaymentSchedule toEntity(CreateInvoicePaymentScheduleCommand cmd) {
        return InvoicePaymentSchedule.builder()
                .invoiceId(cmd.getInvoiceId())
                .installmentNo(cmd.getInstallmentNo() != null ? cmd.getInstallmentNo() : 1)
                .dueDate(cmd.getDueDate())
                .amount(cmd.getAmount() != null ? cmd.getAmount() : BigDecimal.ZERO)
                .paidAmount(cmd.getPaidAmount() != null ? cmd.getPaidAmount() : BigDecimal.ZERO)
                .status(cmd.getStatus() != null ? cmd.getStatus() : PaymentScheduleStatus.pending)
                .bankName(cmd.getBankName()).bankAccount(cmd.getBankAccount())
                .note(cmd.getNote()).build();
    }

    public static InvoicePaymentSchedule toEntity(UpdateInvoicePaymentScheduleCommand cmd, InvoicePaymentSchedule e) {
        return InvoicePaymentSchedule.builder()
                .id(e.getId()).invoiceId(e.getInvoiceId()).installmentNo(e.getInstallmentNo())
                .dueDate(cmd.getDueDate() != null ? cmd.getDueDate() : e.getDueDate())
                .amount(cmd.getAmount() != null ? cmd.getAmount() : e.getAmount())
                .paidAmount(cmd.getPaidAmount() != null ? cmd.getPaidAmount() : e.getPaidAmount())
                .status(cmd.getStatus() != null ? cmd.getStatus() : e.getStatus())
                .bankName(cmd.getBankName() != null ? cmd.getBankName() : e.getBankName())
                .bankAccount(cmd.getBankAccount() != null ? cmd.getBankAccount() : e.getBankAccount())
                .note(cmd.getNote() != null ? cmd.getNote() : e.getNote()).build();
    }

    public static InvoicePaymentScheduleResult toResult(InvoicePaymentSchedule e) {
        return InvoicePaymentScheduleResult.builder()
                .id(e.getId()).invoiceId(e.getInvoiceId()).installmentNo(e.getInstallmentNo())
                .dueDate(e.getDueDate()).amount(e.getAmount()).paidAmount(e.getPaidAmount())
                .status(e.getStatus()).bankName(e.getBankName()).bankAccount(e.getBankAccount())
                .note(e.getNote()).build();
    }

    private InvoicePaymentScheduleCommandMapper() {}
}
