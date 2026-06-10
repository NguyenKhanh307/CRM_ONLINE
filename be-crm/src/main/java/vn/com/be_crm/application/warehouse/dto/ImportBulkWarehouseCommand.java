package vn.com.be_crm.application.warehouse.dto;

import java.util.List;

/** Lệnh nhập hàng loạt Warehouse từ file Excel/CSV. */
public record ImportBulkWarehouseCommand(
        String importType,
        String ownerMode,
        Long specificOwnerId,
        String ownerFileColumn,
        List<ImportWarehouseRowCommand> rows
) {}
