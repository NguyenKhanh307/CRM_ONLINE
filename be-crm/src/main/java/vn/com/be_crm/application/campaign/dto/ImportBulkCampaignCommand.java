package vn.com.be_crm.application.campaign.dto;

import java.util.List;

/** Lệnh nhập hàng loạt Campaign từ file Excel/CSV. */
public record ImportBulkCampaignCommand(
        String importType,
        String ownerMode,
        Long specificOwnerId,
        String ownerFileColumn,
        List<ImportCampaignRowCommand> rows
) {}
