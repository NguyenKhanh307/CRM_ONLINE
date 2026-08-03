import type { ImportOptions, ImportBulkResult } from '@/shared/components/import/importTypes';
import { leadService } from '../services/leadService';

// nhập khẩu hàng loạt tiềm năng từ file Excel/CSV — trả về kết quả thành công/thất bại
export function useImportLeadBulk() {
    return (rows: Record<string, unknown>[], options: ImportOptions): Promise<ImportBulkResult> =>
        leadService.importBulk(rows, options).then(r => r.data.data);
}
