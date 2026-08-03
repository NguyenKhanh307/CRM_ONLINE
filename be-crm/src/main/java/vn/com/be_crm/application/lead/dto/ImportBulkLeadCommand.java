package vn.com.be_crm.application.lead.dto;

import java.util.List;

// lệnh nhập hàng loạt Lead từ file Excel/CSV
public record ImportBulkLeadCommand(
        String importType,
        String ownerMode,
        Long specificOwnerId,
        String ownerFileColumn,
        List<ImportLeadRowCommand> rows
) {}
