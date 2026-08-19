import type { ImportOptions, ImportBulkResult } from '@/shared/components/import/importTypes';
import { quotationService } from '../services/quotationService';

// nhập khẩu hàng loạt báo giá từ file Excel — trả về kết quả thành công/thất bại
export function useImportQuotationBulk() {
    return (rows: Record<string, unknown>[], options: ImportOptions): Promise<ImportBulkResult> =>
        quotationService.importBulk(rows, options).then(r => r.data.data);
}
