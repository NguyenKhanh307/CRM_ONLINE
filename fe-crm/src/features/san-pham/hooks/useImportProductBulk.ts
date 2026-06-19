import type { ImportOptions, ImportBulkResult } from '@/shared/components/import/importTypes';
import { productService } from '../services/productService';

/** Nhập khẩu hàng loạt sản phẩm từ file Excel/CSV — trả về kết quả thành công/thất bại. */
export function useImportProductBulk() {
    return (rows: Record<string, unknown>[], options: ImportOptions): Promise<ImportBulkResult> =>
        productService.importBulk(rows, options).then(r => r.data.data);
}
