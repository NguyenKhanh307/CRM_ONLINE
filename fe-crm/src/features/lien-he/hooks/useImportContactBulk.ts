import type { ImportOptions, ImportBulkResult } from '@/shared/components/import/importTypes';
import { contactService } from '../services/contactService';

/** Nhập khẩu hàng loạt liên hệ từ file Excel/CSV — trả về kết quả thành công/thất bại. */
export function useImportContactBulk() {
    return (rows: Record<string, unknown>[], options: ImportOptions): Promise<ImportBulkResult> =>
        contactService.importBulk(rows, options).then(r => r.data.data);
}
