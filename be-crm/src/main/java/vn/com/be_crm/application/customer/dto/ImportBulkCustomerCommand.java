package vn.com.be_crm.application.customer.dto;

import java.util.List;

/** Lệnh nhập hàng loạt Customer từ file Excel/CSV. */
public record ImportBulkCustomerCommand(
        String importType,
        String ownerMode,
        Long specificOwnerId,
        String ownerFileColumn,
        List<ImportCustomerRowCommand> rows
) {}
