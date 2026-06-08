import { type ColumnDef } from '@tanstack/react-table';
import { formatISODate } from '@/shared/utils/date';
import type { LeadResult } from '../types/leadTypes';

const STATUS_COLORS: Record<string, string> = {
    new_:        'bg-blue-100 text-blue-700',
    contacted:   'bg-yellow-100 text-yellow-700',
    qualified:   'bg-green-100 text-green-700',
    unqualified: 'bg-red-100 text-red-600',
    converted:   'bg-purple-100 text-purple-700',
};

const STATUS_LABELS: Record<string, string> = {
    new_: 'Mới', contacted: 'Đã liên hệ', qualified: 'Tiềm năng',
    unqualified: 'Không tiềm năng', converted: 'Đã chuyển đổi',
};

export const leadColumns: ColumnDef<LeadResult>[] = [
    { accessorKey: 'code', header: 'Mã', size: 100, enableSorting: true },
    { accessorKey: 'name', header: 'Tên tiềm năng', enableSorting: true },
    { accessorKey: 'phone', header: 'Điện thoại', size: 130 },
    { accessorKey: 'email', header: 'Email', enableSorting: true },
    { accessorKey: 'source', header: 'Nguồn', size: 120 },
    {
        accessorKey: 'status',
        header: 'Trạng thái',
        size: 140,
        cell: ({ getValue }) => {
            const s = getValue<string>();
            return (
                <span className={`inline-block px-2 py-0.5 rounded text-sm font-medium ${STATUS_COLORS[s] ?? 'bg-gray-100 text-gray-600'}`}>
                    {STATUS_LABELS[s] ?? s}
                </span>
            );
        },
    },
    {
        accessorKey: 'estimatedValue',
        header: 'Giá trị dự kiến',
        size: 150,
        cell: ({ getValue }) => {
            const v = getValue<number | null>();
            return v != null ? v.toLocaleString('vi-VN') + ' đ' : '—';
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
