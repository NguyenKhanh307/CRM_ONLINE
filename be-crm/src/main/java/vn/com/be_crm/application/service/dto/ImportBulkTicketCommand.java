package vn.com.be_crm.application.service.dto;

import java.util.List;

/** Lệnh nhập hàng loạt Ticket từ file Excel/CSV. */
public record ImportBulkTicketCommand(
        String importType,
        String ownerMode,
        Long specificOwnerId,
        String ownerFileColumn,
        List<ImportTicketRowCommand> rows
) {}
