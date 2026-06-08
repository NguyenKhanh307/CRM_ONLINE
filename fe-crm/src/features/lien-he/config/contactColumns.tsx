import { type ColumnDef } from '@tanstack/react-table';
import { formatISODate } from '@/shared/utils/date';
import type { ContactResult } from '../types/contactTypes';

export const contactColumns: ColumnDef<ContactResult>[] = [
    { accessorKey: 'fullName', header: 'Họ và tên', enableSorting: true },
    { accessorKey: 'position', header: 'Chức vụ', size: 150 },
    { accessorKey: 'email', header: 'Email', enableSorting: true },
    {
        accessorKey: 'isPrimary',
        header: 'Liên hệ chính',
        size: 120,
        cell: ({ getValue }) => (
            <span className={`inline-block px-2 py-0.5 rounded text-sm font-medium ${getValue<boolean>() ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-500'}`}>
                {getValue<boolean>() ? 'Chính' : 'Phụ'}
            </span>
        ),
    },
    { accessorKey: 'address', header: 'Địa chỉ' },
    {
        accessorKey: 'createdAt',
        header: 'Ngày tạo',
        size: 120,
        enableSorting: true,
        cell: ({ getValue }) => formatISODate(getValue<string>()),
    },
];
