package vn.com.be_crm.application.service.mapper;

import vn.com.be_crm.application.service.dto.CreateTicketReturnItemCommand;
import vn.com.be_crm.application.service.dto.TicketReturnItemResult;
import vn.com.be_crm.application.service.dto.UpdateTicketReturnItemCommand;
import vn.com.be_crm.domain.service.entity.TicketReturnItem;

import java.math.BigDecimal;

// chuyển đổi Command <-> TicketReturnItem <-> TicketReturnItemResult
public class TicketReturnItemCommandMapper {

    public static TicketReturnItem toEntity(CreateTicketReturnItemCommand cmd) {
        return TicketReturnItem.builder()
                .ticketId(cmd.getTicketId()).invoiceItemId(cmd.getInvoiceItemId())
                .quantity(cmd.getQuantity() != null ? cmd.getQuantity() : BigDecimal.ONE)
                .reason(cmd.getReason()).conditionNote(cmd.getConditionNote())
                .build();
    }

    public static TicketReturnItem toEntity(UpdateTicketReturnItemCommand cmd, TicketReturnItem e) {
        return e.toBuilder()
                .invoiceItemId(cmd.getInvoiceItemId() != null ? cmd.getInvoiceItemId() : e.getInvoiceItemId())
                .quantity(cmd.getQuantity() != null ? cmd.getQuantity() : e.getQuantity())
                .reason(cmd.getReason() != null ? cmd.getReason() : e.getReason())
                .conditionNote(cmd.getConditionNote() != null ? cmd.getConditionNote() : e.getConditionNote())
                .build();
    }

    public static TicketReturnItemResult toResult(TicketReturnItem e) {
        return TicketReturnItemResult.builder()
                .id(e.getId()).ticketId(e.getTicketId()).invoiceItemId(e.getInvoiceItemId())
                .quantity(e.getQuantity())
                .reason(e.getReason()).conditionNote(e.getConditionNote())
                .build();
    }

    private TicketReturnItemCommandMapper() {}
}
