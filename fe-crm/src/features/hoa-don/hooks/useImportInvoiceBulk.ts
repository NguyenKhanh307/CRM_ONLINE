import type { ImportOptions, ImportBulkResult } from '@/shared/components/import/importTypes';
import { invoiceService } from '../services/invoiceService';

// nhập khẩu hàng loạt Hóa đơn từ file Excel/CSV — trả về kết quả thành công/thất bại
export function useImportInvoiceBulk() {
    return (rows: Record<string, unknown>[], options: ImportOptions): Promise<ImportBulkResult> =>
        invoiceService.importBulk(rows, options).then(r => r.data.data);
}
