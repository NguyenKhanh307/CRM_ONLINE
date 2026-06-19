import type { ImportOptions, ImportBulkResult } from '@/shared/components/import/importTypes';
import { warehouseService } from '../services/warehouseService';

/** Nhập khẩu hàng loạt kho hàng từ file Excel/CSV — trả về kết quả thành công/thất bại. */
export function useImportWarehouseBulk() {
    return (rows: Record<string, unknown>[], options: ImportOptions): Promise<ImportBulkResult> =>
        warehouseService.importBulk(rows, options).then(r => r.data.data);
}
