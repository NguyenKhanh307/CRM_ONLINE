package vn.com.be_crm.application.invoice.dto;

import java.util.List;

/** Lệnh nhập hàng loạt Invoice từ file Excel/CSV. */
public record ImportBulkInvoiceCommand(
        String importType,
        String ownerMode,
        Long specificOwnerId,
        String ownerFileColumn,
        List<ImportInvoiceRowCommand> rows
) {}
