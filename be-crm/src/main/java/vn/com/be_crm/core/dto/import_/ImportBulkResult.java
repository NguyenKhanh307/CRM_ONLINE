package vn.com.be_crm.core.dto.import_;

import java.util.List;

/** Kết quả nhập hàng loạt từ file Excel/CSV. */
public record ImportBulkResult(int successCount, int failedCount, List<ImportRowError> errors) {}
