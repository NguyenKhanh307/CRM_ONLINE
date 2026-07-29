import { formatISODate } from '@/shared/utils/date';
import type { ExportColumn } from '@/shared/components/export/exportTypes';
import type { OpportunityResult } from '../types/opportunityTypes';

const STATUS_LABELS: Record<string, string> = {
    open: 'Đang mở', won: 'Đã thắng', lost: 'Đã thua',
};

/** Các cột khả dụng khi xuất file phân hệ Cơ hội. */
export const opportunityExportColumns: ExportColumn<OpportunityResult>[] = [
    { key: 'code', label: 'Mã' },
    { key: 'name', label: 'Tên cơ hội' },
    { key: 'amount', label: 'Giá trị', format: r => r.amount ?? '' },
    { key: 'probability', label: 'Xác suất (%)', format: r => r.probability ?? '' },
    { key: 'status', label: 'Trạng thái', format: r => STATUS_LABELS[r.status] ?? r.status },
    { key: 'expectedCloseDate', label: 'Ngày đóng dự kiến', format: r => r.expectedCloseDate ? formatISODate(r.expectedCloseDate) : '' },
    { key: 'campaignName', label: 'Chiến dịch', format: r => r.campaignName ?? '' },
    { key: 'createdAt', label: 'Ngày tạo', format: r => formatISODate(r.createdAt) },
];
