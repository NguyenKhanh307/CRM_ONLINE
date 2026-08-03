import type { ExportColumn } from '@/shared/components/export/exportTypes';
import { formatISODate } from '@/shared/utils/date';
import type { TicketResult } from '../types/ticketTypes';
import {
    TYPE_LABELS, STATUS_LABELS, PRIORITY_LABELS, CHANNEL_LABELS, RESOLUTION_LABELS,
} from './ticketEnums';

// cột xuất file cho phiếu hỗ trợ — ghi giá trị thuần đã format (nhãn enum, ngày dd/mm/yyyy)
export const ticketExportColumns: ExportColumn<TicketResult>[] = [
    { key: 'code', label: 'Mã phiếu' },
    { key: 'subject', label: 'Tiêu đề' },
    { key: 'type', label: 'Loại', format: r => TYPE_LABELS[r.type] ?? r.type },
    { key: 'status', label: 'Trạng thái', format: r => STATUS_LABELS[r.status] ?? r.status },
    { key: 'priority', label: 'Ưu tiên', format: r => PRIORITY_LABELS[r.priority] ?? r.priority },
    { key: 'channel', label: 'Kênh', format: r => CHANNEL_LABELS[r.channel] ?? r.channel },
    { key: 'resolutionType', label: 'Hình thức giải quyết', format: r => r.resolutionType ? (RESOLUTION_LABELS[r.resolutionType] ?? r.resolutionType) : '' },
    { key: 'slaDueAt', label: 'Hạn SLA', format: r => r.slaDueAt ? formatISODate(r.slaDueAt) : '' },
    { key: 'satisfactionScore', label: 'CSAT', format: r => r.satisfactionScore ?? '' },
    { key: 'createdAt', label: 'Ngày tạo', format: r => formatISODate(r.createdAt) },
];
