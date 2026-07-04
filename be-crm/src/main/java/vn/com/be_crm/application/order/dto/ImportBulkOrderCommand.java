package vn.com.be_crm.application.order.dto;

import java.util.List;

/** Lệnh nhập hàng loạt Order từ file Excel/CSV. */
public record ImportBulkOrderCommand(
        String importType,
        String ownerMode,
        Long specificOwnerId,
        String ownerFileColumn,
        List<ImportOrderRowCommand> rows
) {}
