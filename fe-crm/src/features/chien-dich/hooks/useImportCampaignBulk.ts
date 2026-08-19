import type { ImportOptions, ImportBulkResult } from '@/shared/components/import/importTypes';
import { campaignService } from '../services/campaignService';

// nhập khẩu hàng loạt Chiến dịch từ file Excel — trả về kết quả thành công/thất bại
export function useImportCampaignBulk() {
    return (rows: Record<string, unknown>[], options: ImportOptions): Promise<ImportBulkResult> =>
        campaignService.importBulk(rows, options).then(r => r.data.data);
}
