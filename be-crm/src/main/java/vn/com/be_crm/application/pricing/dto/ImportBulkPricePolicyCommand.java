package vn.com.be_crm.application.pricing.dto;

import java.util.List;

/** Lệnh nhập hàng loạt PricePolicy từ file Excel/CSV. */
public record ImportBulkPricePolicyCommand(
        String importType,
        List<ImportPricePolicyRowCommand> rows
) {}
