package vn.com.be_crm.application.activity.dto;

import java.util.List;

/** Lệnh nhập hàng loạt Activity từ file Excel/CSV. */
public record ImportBulkActivityCommand(
        String importType,
        String ownerMode,
        Long specificOwnerId,
        String ownerFileColumn,
        List<ImportActivityRowCommand> rows
) {}
