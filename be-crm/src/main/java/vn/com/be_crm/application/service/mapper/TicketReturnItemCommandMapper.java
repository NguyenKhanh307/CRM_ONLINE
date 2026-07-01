package vn.com.be_crm.application.service.mapper;

import vn.com.be_crm.application.service.dto.CreateTicketReturnItemCommand;
import vn.com.be_crm.application.service.dto.TicketReturnItemResult;
import vn.com.be_crm.application.service.dto.UpdateTicketReturnItemCommand;
import vn.com.be_crm.domain.service.entity.TicketReturnItem;

import java.math.BigDecimal;

/** Chuyển đổi Command ↔ TicketReturnItem ↔ TicketReturnItemResult. */
public class TicketReturnItemCommandMapper {

    /**
     * Tạo TicketReturnItem từ CreateTicketReturnItemCommand.
     * @param cmd command tạo mới @return domain entity
     */
    public static TicketReturnItem toEntity(CreateTicketReturnItemCommand cmd) {
        return TicketReturnItem.builder()
                .ticketId(cmd.getTicketId()).invoiceItemId(cmd.getInvoiceItemId()).productId(cmd.getProductId())
                .quantity(cmd.getQuantity() != null ? cmd.getQuantity() : BigDecimal.ONE)
                .unitPrice(cmd.getUnitPrice() != null ? cmd.getUnitPrice() : BigDecimal.ZERO)
                .amount(cmd.getAmount() != null ? cmd.getAmount() : BigDecimal.ZERO)
                .reason(cmd.getReason()).conditionNote(cmd.getConditionNote())
                .build();
    }

    /**
     * Cập nhật TicketReturnItem từ UpdateTicketReturnItemCommand.
     * @param cmd command cập nhật @param e entity hiện tại @return domain entity đã cập nhật
     */
    public static TicketReturnItem toEntity(UpdateTicketReturnItemCommand cmd, TicketReturnItem e) {
        return e.toBuilder()
                .invoiceItemId(cmd.getInvoiceItemId() != null ? cmd.getInvoiceItemId() : e.getInvoiceItemId())
                .productId(cmd.getProductId() != null ? cmd.getProductId() : e.getProductId())
                .quantity(cmd.getQuantity() != null ? cmd.getQuantity() : e.getQuantity())
                .unitPrice(cmd.getUnitPrice() != null ? cmd.getUnitPrice() : e.getUnitPrice())
                .amount(cmd.getAmount() != null ? cmd.getAmount() : e.getAmount())
                .reason(cmd.getReason() != null ? cmd.getReason() : e.getReason())
                .conditionNote(cmd.getConditionNote() != null ? cmd.getConditionNote() : e.getConditionNote())
                .build();
    }

    /**
     * Chuyển TicketReturnItem sang TicketReturnItemResult.
     * @param e domain entity @return result DTO
     */
    public static TicketReturnItemResult toResult(TicketReturnItem e) {
        return TicketReturnItemResult.builder()
                .id(e.getId()).ticketId(e.getTicketId()).invoiceItemId(e.getInvoiceItemId())
                .productId(e.getProductId()).quantity(e.getQuantity()).unitPrice(e.getUnitPrice())
                .amount(e.getAmount()).reason(e.getReason()).conditionNote(e.getConditionNote())
                .build();
    }

    private TicketReturnItemCommandMapper() {}
}
