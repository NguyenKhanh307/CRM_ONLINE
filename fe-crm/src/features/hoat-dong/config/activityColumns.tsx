import { type ColumnDef } from '@tanstack/react-table';
import { badgeCell, dateCell, fkCell, labelCell, numberCell, textCell } from '@/shared/components/table/cells';
import type { ActivityResult } from '../types/activityTypes';

const TYPE_LABELS: Record<string, string> = {
    call: 'Cuộc gọi', email: 'Email', meeting: 'Cuộc họp',
    task: 'Nhiệm vụ', note: 'Ghi chú',
};

const STATUS_COLORS: Record<string, string> = {
    planned: 'bg-gray-100 text-gray-600',
    in_progress: 'bg-yellow-100 text-yellow-700',
    done: 'bg-green-100 text-green-700',
    cancelled: 'bg-red-100 text-red-600',
};

const STATUS_LABELS: Record<string, string> = {
    planned: 'Đã lên kế hoạch', in_progress: 'Đang thực hiện', done: 'Hoàn thành', cancelled: 'Đã hủy',
};

const PRIORITY_LABELS: Record<string, string> = {
    low: 'Thấp', medium: 'Trung bình', high: 'Cao', urgent: 'Khẩn cấp',
};

const CALL_DIRECTION_LABELS: Record<string, string> = {
    inbound: 'Gọi đến', outbound: 'Gọi đi',
};

/** Map ID → tên cho các cột khóa ngoại của Hoạt động. */
export interface ActivityColumnLookups {
    users: Map<number, string>;
}

/** Tạo danh sách cột Hoạt động — hiển thị đầy đủ trường + tên người phụ trách. */
export const getActivityColumns = (lk: ActivityColumnLookups): ColumnDef<ActivityResult>[] => [
    { accessorKey: 'type', header: 'Loại', size: 120, cell: labelCell(TYPE_LABELS) },
    { accessorKey: 'subject', header: 'Tiêu đề', size: 200, enableSorting: true },
    { accessorKey: 'content', header: 'Nội dung', size: 220, cell: textCell },
    { accessorKey: 'priority', header: 'Ưu tiên', size: 110, cell: labelCell(PRIORITY_LABELS) },
    { accessorKey: 'status', header: 'Trạng thái', size: 130, cell: badgeCell(STATUS_LABELS, STATUS_COLORS) },
    { accessorKey: 'targetType', header: 'Đối tượng', size: 120, cell: textCell },
    { accessorKey: 'targetId', header: 'ID đối tượng', size: 110, cell: numberCell },
    { accessorKey: 'relatedType', header: 'Liên quan', size: 120, cell: textCell },
    { accessorKey: 'relatedId', header: 'ID liên quan', size: 110, cell: numberCell },
    { accessorKey: 'assignedUserId', header: 'Người phụ trách', size: 160, cell: fkCell(lk.users) },
    { accessorKey: 'location', header: 'Địa điểm', size: 160, cell: textCell },
    { accessorKey: 'callDirection', header: 'Hướng gọi', size: 110, cell: labelCell(CALL_DIRECTION_LABELS) },
    { accessorKey: 'callResult', header: 'Kết quả gọi', size: 150, cell: textCell },
    { accessorKey: 'callDuration', header: 'Thời lượng (s)', size: 120, cell: numberCell },
    { accessorKey: 'dueAt', header: 'Hạn hoàn thành', size: 160, cell: dateCell },
    { accessorKey: 'completedAt', header: 'Ngày hoàn thành', size: 160, cell: dateCell },
    { accessorKey: 'createdAt', header: 'Ngày tạo', size: 120, enableSorting: true, cell: dateCell },
];
