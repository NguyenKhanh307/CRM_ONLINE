package vn.com.be_crm.application.quotation.dto;

import java.util.List;

/** Lệnh nhập hàng loạt Quotation từ file Excel/CSV. */
public record ImportBulkQuotationCommand(
        String importType,
        String ownerMode,
        Long specificOwnerId,
        String ownerFileColumn,
        List<ImportQuotationRowCommand> rows
) {}
