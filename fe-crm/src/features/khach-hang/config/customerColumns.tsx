import { type ColumnDef } from '@tanstack/react-table';
import { formatISODate } from '@/shared/utils/date';
import type { CustomerResult } from '../types/customerTypes';

const TYPE_LABELS: Record<string, string> = {
    individual: 'Cá nhân', company: 'Công ty', agency: 'Đại lý',
};

const STATUS_COLORS: Record<string, string> = {
    active: 'bg-green-100 text-green-700',
    inactive: 'bg-red-100 text-red-600',
    prospect: 'bg-blue-100 text-blue-700',
};

export const customerColumns: ColumnDef<CustomerResult>[] = [
    { accessorKey: 'code', header: 'Mã KH', size: 100, enableSorting: true },
    { accessorKey: 'name', header: 'Tên khách hàng', enableSorting: true },
    {
        accessorKey: 'type',
        header: 'Loại',
        size: 100,
        cell: ({ getValue }) => TYPE_LABELS[getValue<string>()] ?? getValue<string>(),
    },
    { accessorKey: 'phone', header: 'Điện thoại', size: 130 },
    { accessorKey: 'email', header: 'Email', enableSorting: true },
    {
        accessorKey: 'status',
        header: 'Trạng thái',
        size: 130,
        cell: ({ getValue }) => {
            const s = getValue<string>();
            return (
                <span className={`inline-block px-2 py-0.5 rounded text-sm font-medium ${STATUS_COLORS[s] ?? 'bg-gray-100 text-gray-600'}`}>
                    {s === 'active' ? 'Hoạt động' : s === 'inactive' ? 'Không hoạt động' : s}
                </span>
            );
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
