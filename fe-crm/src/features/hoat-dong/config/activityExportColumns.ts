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

const PRIORITY_LABELS: Record<string, string> = {
    low: 'Thấp', medium: 'Trung bình', high: 'Cao', urgent: 'Khẩn cấp',
};

const CALL_DIRECTION_LABELS: Record<string, string> = {
    in: 'Gọi đến', out: 'Gọi đi',
};

// các cột khả dụng khi xuất file phân hệ Hoạt động
export const activityExportColumns: ExportColumn<ActivityResult>[] = [
    { key: 'type', label: 'Loại', format: r => TYPE_LABELS[r.type] ?? r.type },
    { key: 'subject', label: 'Tiêu đề' },
    { key: 'content', label: 'Nội dung', format: r => r.content ?? '' },
    { key: 'priority', label: 'Ưu tiên', format: r => (r.priority ? (PRIORITY_LABELS[r.priority] ?? r.priority) : '') },
    { key: 'targetType', label: 'Đối tượng' },
    { key: 'targetId', label: 'ID đối tượng', format: r => r.targetId ?? '' },
    { key: 'assignedUserName', label: 'Người phụ trách', format: r => r.assignedUserName ?? '' },
    { key: 'location', label: 'Địa điểm', format: r => r.location ?? '' },
    { key: 'callDirection', label: 'Hướng gọi', format: r => (r.callDirection ? (CALL_DIRECTION_LABELS[r.callDirection] ?? r.callDirection) : '') },
    { key: 'callResult', label: 'Kết quả gọi', format: r => r.callResult ?? '' },
    { key: 'callDuration', label: 'Thời lượng (phút)', format: r => r.callDuration ?? '' },
    { key: 'status', label: 'Trạng thái', format: r => STATUS_LABELS[r.status] ?? r.status },
    { key: 'dueAt', label: 'Hạn hoàn thành', format: r => (r.dueAt ? formatISODate(r.dueAt) : '') },
    { key: 'createdAt', label: 'Ngày tạo', format: r => formatISODate(r.createdAt) },
];
