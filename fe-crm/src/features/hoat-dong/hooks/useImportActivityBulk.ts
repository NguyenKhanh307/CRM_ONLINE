import type { ImportOptions, ImportBulkResult } from '@/shared/components/import/importTypes';
import { activityService } from '../services/activityService';

/** Nhập khẩu hàng loạt hoạt động từ file Excel/CSV — trả về kết quả thành công/thất bại. */
export function useImportActivityBulk() {
    return (rows: Record<string, unknown>[], options: ImportOptions): Promise<ImportBulkResult> =>
        activityService.importBulk(rows, options).then(r => r.data.data);
}
