import { type ColumnDef } from '@tanstack/react-table';
import { formatISODate } from '@/shared/utils/date';
import type { WarehouseResult } from '../types/warehouseTypes';

export const warehouseColumns: ColumnDef<WarehouseResult>[] = [
    { accessorKey: 'code', header: 'Mã kho', size: 110, enableSorting: true },
    { accessorKey: 'name', header: 'Tên kho hàng', enableSorting: true },
    { accessorKey: 'address', header: 'Địa chỉ' },
    {
        accessorKey: 'isActive',
        header: 'Trạng thái',
        size: 120,
        cell: ({ getValue }) => (
            <span className={`inline-block px-2 py-0.5 rounded text-sm font-medium ${getValue<boolean>() ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-600'}`}>
                {getValue<boolean>() ? 'Đang hoạt động' : 'Ngừng hoạt động'}
            </span>
        ),
    },
    {
        accessorKey: 'createdAt',
        header: 'Ngày tạo',
        size: 120,
        enableSorting: true,
        cell: ({ getValue }) => formatISODate(getValue<string>()),
    },
    {
        accessorKey: 'updatedAt',
        header: 'Ngày cập nhật',
        size: 130,
        enableSorting: true,
        cell: ({ getValue }) => {
            const v = getValue<string | null>();
            return v ? formatISODate(v) : '—';
        },
    },
];
