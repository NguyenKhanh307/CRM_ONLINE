import type { ImportOptions, ImportBulkResult } from '@/shared/components/import/importTypes';
import { pricingService } from '../services/pricingService';

// nhập khẩu hàng loạt chính sách giá từ file Excel — trả về kết quả thành công/thất bại
export function useImportPricePolicyBulk() {
    return (rows: Record<string, unknown>[], options: ImportOptions): Promise<ImportBulkResult> =>
        pricingService.importBulk(rows, options).then(r => r.data.data);
}
