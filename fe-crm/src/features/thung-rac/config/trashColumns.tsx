import { type ColumnDef } from '@tanstack/react-table';
import { formatISODate } from '@/shared/utils/date';
import type { DeletedItemRow } from '../types/thungRacTypes';

export const trashColumns: ColumnDef<DeletedItemRow>[] = [
    { accessorKey: 'id', header: 'ID', size: 70 },
    { accessorKey: 'displayName', header: 'Tên / Mã', enableSorting: true },
    { accessorKey: 'deletedByName', header: 'Người xóa', size: 180,
        cell: ({ getValue }) => getValue<string | null>() ?? '—' },
    {
        accessorKey: 'deletedAt',
        header: 'Ngày xóa',
        size: 140,
        enableSorting: true,
        cell: ({ getValue }) => formatISODate(getValue<string>()),
    },
];
