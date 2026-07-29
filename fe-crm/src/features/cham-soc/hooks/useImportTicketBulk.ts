import type { ImportOptions, ImportBulkResult } from '@/shared/components/import/importTypes';
import { ticketService } from '../services/ticketService';

/** Nhập khẩu hàng loạt phiếu hỗ trợ từ file Excel/CSV — trả về kết quả thành công/thất bại. */
export function useImportTicketBulk() {
    return (rows: Record<string, unknown>[], options: ImportOptions): Promise<ImportBulkResult> =>
        ticketService.importBulk(rows, options).then(r => r.data.data);
}
