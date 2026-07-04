import { formatISODate } from '@/shared/utils/date';
import type { ExportColumn } from '@/shared/components/export/exportTypes';
import type { CampaignResult } from '../types/campaignTypes';
import { CAMPAIGN_STATUS_LABELS, CAMPAIGN_TYPE_LABELS } from './campaignColumns';

/** Các cột khả dụng khi xuất file phân hệ Chiến dịch. */
export const campaignExportColumns: ExportColumn<CampaignResult>[] = [
    { key: 'code', label: 'Mã' },
    { key: 'name', label: 'Tên chiến dịch' },
    { key: 'type', label: 'Loại', format: r => CAMPAIGN_TYPE_LABELS[r.type] ?? r.type },
    { key: 'status', label: 'Trạng thái', format: r => CAMPAIGN_STATUS_LABELS[r.status] ?? r.status },
    { key: 'channel', label: 'Kênh', format: r => r.channel ?? '' },
    { key: 'budget', label: 'Ngân sách', format: r => r.budget ?? '' },
    { key: 'startDate', label: 'Bắt đầu' },
    { key: 'endDate', label: 'Kết thúc' },
    { key: 'createdAt', label: 'Ngày tạo', format: r => formatISODate(r.createdAt) },
];
