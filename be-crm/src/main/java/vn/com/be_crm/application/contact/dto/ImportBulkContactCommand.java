package vn.com.be_crm.application.contact.dto;

import java.util.List;

/** Lệnh nhập hàng loạt Contact từ file Excel/CSV. */
public record ImportBulkContactCommand(
        String importType,
        String ownerMode,
        Long specificOwnerId,
        String ownerFileColumn,
        List<ImportContactRowCommand> rows
) {}
