package vn.com.be_crm.application.opportunity.dto;

import java.util.List;

/** Lệnh nhập hàng loạt Opportunity từ file Excel/CSV. */
public record ImportBulkOpportunityCommand(
        String importType,
        String ownerMode,
        Long specificOwnerId,
        String ownerFileColumn,
        List<ImportOpportunityRowCommand> rows
) {}
