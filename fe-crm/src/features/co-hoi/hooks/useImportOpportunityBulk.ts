import type { ImportOptions, ImportBulkResult } from '@/shared/components/import/importTypes';
import { opportunityService } from '../services/opportunityService';

/** Nhập khẩu hàng loạt cơ hội từ file Excel/CSV — trả về kết quả thành công/thất bại. */
export function useImportOpportunityBulk() {
    return (rows: Record<string, unknown>[], options: ImportOptions): Promise<ImportBulkResult> =>
        opportunityService.importBulk(rows, options).then(r => r.data.data);
}
