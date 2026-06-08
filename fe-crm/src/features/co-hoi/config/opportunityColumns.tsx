import { type ColumnDef } from '@tanstack/react-table';
import { formatISODate } from '@/shared/utils/date';
import type { OpportunityResult } from '../types/opportunityTypes';

const STATUS_COLORS: Record<string, string> = {
    open: 'bg-blue-100 text-blue-700',
    won: 'bg-green-100 text-green-700',
    lost: 'bg-red-100 text-red-600',
};

const STATUS_LABELS: Record<string, string> = {
    open: 'Đang mở', won: 'Đã thắng', lost: 'Đã thua',
};

export const opportunityColumns: ColumnDef<OpportunityResult>[] = [
    { accessorKey: 'code', header: 'Mã', size: 100, enableSorting: true },
    { accessorKey: 'name', header: 'Tên cơ hội', enableSorting: true },
    {
        accessorKey: 'amount',
        header: 'Giá trị',
        size: 140,
        cell: ({ getValue }) => {
            const v = getValue<number | null>();
            return v != null ? v.toLocaleString('vi-VN') + ' đ' : '—';
        },
    },
    {
        accessorKey: 'probability',
        header: 'Xác suất',
        size: 100,
        cell: ({ getValue }) => {
            const v = getValue<number | null>();
            return v != null ? v + '%' : '—';
        },
    },
    {
        accessorKey: 'status',
        header: 'Trạng thái',
        size: 120,
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
        accessorKey: 'expectedCloseDate',
        header: 'Ngày đóng dự kiến',
        size: 160,
        cell: ({ getValue }) => getValue<string | null>() ?? '—',
    },
    {
        accessorKey: 'createdAt',
        header: 'Ngày tạo',
        size: 120,
        enableSorting: true,
        cell: ({ getValue }) => formatISODate(getValue<string>()),
    },
];
