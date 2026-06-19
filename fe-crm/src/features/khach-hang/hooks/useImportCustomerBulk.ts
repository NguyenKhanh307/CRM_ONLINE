import type { ImportOptions, ImportBulkResult } from '@/shared/components/import/importTypes';
import { customerService } from '../services/customerService';

/** Nhập khẩu hàng loạt khách hàng từ file Excel/CSV — trả về kết quả thành công/thất bại. */
export function useImportCustomerBulk() {
    return (rows: Record<string, unknown>[], options: ImportOptions): Promise<ImportBulkResult> =>
        customerService.importBulk(rows, options).then(r => r.data.data);
}
