import { formatISODate } from '@/shared/utils/date';
import type { ExportColumn } from '@/shared/components/export/exportTypes';
import type { ActivityResult } from '../types/activityTypes';

const TYPE_LABELS: Record<string, string> = {
    call: 'Cuộc gọi', email: 'Email', meeting: 'Cuộc họp',
    task: 'Nhiệm vụ', note: 'Ghi chú',
};

const STATUS_LABELS: Record<string, string> = {
    planned: 'Đã lên kế hoạch', in_progress: 'Đang thực hiện', done: 'Hoàn thành', cancelled: 'Đã hủy',
};

/** Các cột khả dụng khi xuất file phân hệ Hoạt động. */
export const activityExportColumns: ExportColumn<ActivityResult>[] = [
    { key: 'type', label: 'Loại', format: r => TYPE_LABELS[r.type] ?? r.type },
    { key: 'subject', label: 'Tiêu đề' },
    { key: 'targetType', label: 'Đối tượng' },
    { key: 'status', label: 'Trạng thái', format: r => STATUS_LABELS[r.status] ?? r.status },
    { key: 'dueAt', label: 'Hạn hoàn thành', format: r => (r.dueAt ? formatISODate(r.dueAt) : '') },
    { key: 'createdAt', label: 'Ngày tạo', format: r => formatISODate(r.createdAt) },
];
