import { type ColumnDef } from '@tanstack/react-table';
import { formatISODate } from '@/shared/utils/date';
import type { PricePolicyResult, PricePolicyStatus } from '../types/pricingTypes';

const STATUS_LABEL: Record<PricePolicyStatus, string> = {
    active: 'Đang áp dụng',
    inactive: 'Ngừng áp dụng',
    expired: 'Hết hạn',
};

const STATUS_CLASS: Record<PricePolicyStatus, string> = {
    active: 'bg-green-100 text-green-700',
    inactive: 'bg-gray-100 text-gray-600',
    expired: 'bg-red-100 text-red-600',
};

export const pricingColumns: ColumnDef<PricePolicyResult>[] = [
    { accessorKey: 'code', header: 'Mã chính sách', size: 130, enableSorting: true },
    { accessorKey: 'name', header: 'Tên chính sách', enableSorting: true },
    { accessorKey: 'type', header: 'Loại', size: 110 },
    { accessorKey: 'priority', header: 'Ưu tiên', size: 80, enableSorting: true },
    {
        accessorKey: 'startDate',
        header: 'Ngày bắt đầu',
        size: 120,
        cell: ({ getValue }) => getValue<string | null>() ? formatISODate(getValue<string>()) : '—',
    },
    {
        accessorKey: 'endDate',
        header: 'Ngày kết thúc',
        size: 120,
        cell: ({ getValue }) => getValue<string | null>() ? formatISODate(getValue<string>()) : '—',
    },
    {
        accessorKey: 'status',
        header: 'Trạng thái',
        size: 130,
        cell: ({ getValue }) => {
            const s = getValue<PricePolicyStatus>();
            return (
                <span className={`inline-block px-2 py-0.5 rounded text-sm font-medium ${STATUS_CLASS[s]}`}>
                    {STATUS_LABEL[s]}
                </span>
            );
        },
    },
];
