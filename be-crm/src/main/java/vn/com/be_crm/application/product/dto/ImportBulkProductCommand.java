package vn.com.be_crm.application.product.dto;

import java.util.List;

/** Lệnh nhập hàng loạt Product từ file Excel/CSV. */
public record ImportBulkProductCommand(
        String importType,
        String ownerMode,
        Long specificOwnerId,
        String ownerFileColumn,
        List<ImportProductRowCommand> rows
) {}
