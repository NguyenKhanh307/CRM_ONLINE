package vn.com.be_crm.application.shared.dto;

import java.util.List;

/** Kết quả nhập hàng loạt từ file Excel/CSV. */
public record ImportBulkResult(int successCount, int failedCount, List<ImportRowError> errors) {}
