import type { ImportOptions, ImportBulkResult } from '@/shared/components/import/importTypes';
import { orderService } from '../services/orderService';

// nhập khẩu hàng loạt Đơn hàng từ file Excel/CSV — trả về kết quả thành công/thất bại
export function useImportOrderBulk() {
    return (rows: Record<string, unknown>[], options: ImportOptions): Promise<ImportBulkResult> =>
        orderService.importBulk(rows, options).then(r => r.data.data);
}
