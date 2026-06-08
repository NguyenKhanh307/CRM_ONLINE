import { type ColumnDef } from '@tanstack/react-table';
import { formatISODate } from '@/shared/utils/date';
import type { ProductResult } from '../types/productTypes';

export const productColumns: ColumnDef<ProductResult>[] = [
    { accessorKey: 'sku', header: 'Mã SKU', size: 120, enableSorting: true },
    { accessorKey: 'name', header: 'Tên sản phẩm', enableSorting: true },
    { accessorKey: 'type', header: 'Loại', size: 100 },
    { accessorKey: 'unit', header: 'Đơn vị', size: 80 },
    {
        accessorKey: 'basePrice',
        header: 'Giá bán',
        size: 140,
        cell: ({ getValue }) => {
            const v = getValue<number | null>();
            return v != null ? v.toLocaleString('vi-VN') + ' đ' : '—';
        },
    },
    {
        accessorKey: 'isActive',
        header: 'Trạng thái',
        size: 120,
        cell: ({ getValue }) => (
            <span className={`inline-block px-2 py-0.5 rounded text-sm font-medium ${getValue<boolean>() ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-600'}`}>
                {getValue<boolean>() ? 'Đang bán' : 'Ngừng bán'}
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
];
