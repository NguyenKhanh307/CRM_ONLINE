import { type ColumnDef } from '@tanstack/react-table';
import { formatISODate } from '@/shared/utils/date';
import type { ActivityResult } from '../types/activityTypes';

const TYPE_LABELS: Record<string, string> = {
    call: 'Cuộc gọi', email: 'Email', meeting: 'Cuộc họp',
    task: 'Nhiệm vụ', note: 'Ghi chú',
};

const STATUS_COLORS: Record<string, string> = {
    pending: 'bg-yellow-100 text-yellow-700',
    completed: 'bg-green-100 text-green-700',
    cancelled: 'bg-red-100 text-red-600',
};

export const activityColumns: ColumnDef<ActivityResult>[] = [
    {
        accessorKey: 'type',
        header: 'Loại',
        size: 120,
        cell: ({ getValue }) => TYPE_LABELS[getValue<string>()] ?? getValue<string>(),
    },
    { accessorKey: 'subject', header: 'Tiêu đề', enableSorting: true },
    { accessorKey: 'targetType', header: 'Đối tượng', size: 120 },
    {
        accessorKey: 'status',
        header: 'Trạng thái',
        size: 130,
        cell: ({ getValue }) => {
            const s = getValue<string>();
            return (
                <span className={`inline-block px-2 py-0.5 rounded text-sm font-medium ${STATUS_COLORS[s] ?? 'bg-gray-100 text-gray-600'}`}>
                    {s === 'pending' ? 'Chờ xử lý' : s === 'completed' ? 'Hoàn thành' : s}
                </span>
            );
        },
    },
    {
        accessorKey: 'dueAt',
        header: 'Hạn hoàn thành',
        size: 160,
        cell: ({ getValue }) => {
            const v = getValue<string | null>();
            return v ? formatISODate(v) : '—';
        },
    },
    {
        accessorKey: 'createdAt',
        header: 'Ngày tạo',
        size: 120,
        enableSorting: true,
        cell: ({ getValue }) => formatISODate(getValue<string>()),
    },
];
