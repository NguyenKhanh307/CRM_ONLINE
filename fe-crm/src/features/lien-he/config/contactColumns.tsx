import { type ColumnDef } from '@tanstack/react-table';
import { dateCell, labelCell, textCell, yesNoCell } from '@/shared/components/table/cells';
import type { ContactResult } from '../types/contactTypes';

const GENDER_LABELS: Record<string, string> = {
    male: 'Nam', female: 'Nữ', other: 'Khác',
};

/** Tạo danh sách cột Liên hệ — hiển thị đầy đủ trường + tên khóa ngoại (do BE resolve sẵn). */
export const getContactColumns = (): ColumnDef<ContactResult>[] => [
    { accessorKey: 'salutation', header: 'Xưng hô', size: 100, cell: textCell },
    { accessorKey: 'fullName', header: 'Họ và tên', size: 180, enableSorting: true },
    { accessorKey: 'title', header: 'Chức danh', size: 140, cell: textCell },
    { accessorKey: 'department', header: 'Phòng ban', size: 140, cell: textCell },
    { accessorKey: 'position', header: 'Chức vụ', size: 150, cell: textCell },
    { accessorKey: 'email', header: 'Email', size: 180, enableSorting: true, cell: textCell },
    { accessorKey: 'workEmail', header: 'Email công việc', size: 180, cell: textCell },
    { accessorKey: 'personalEmail', header: 'Email cá nhân', size: 180, cell: textCell },
    { accessorKey: 'zalo', header: 'Zalo', size: 130, cell: textCell },
    { accessorKey: 'source', header: 'Nguồn', size: 120, cell: textCell },
    { accessorKey: 'gender', header: 'Giới tính', size: 100, cell: labelCell(GENDER_LABELS) },
    { accessorKey: 'dateOfBirth', header: 'Ngày sinh', size: 120, cell: dateCell },
    { accessorKey: 'address', header: 'Địa chỉ', size: 200, cell: textCell },
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
    { accessorKey: 'doNotCall', header: 'Không gọi', size: 100, cell: yesNoCell },
    { accessorKey: 'doNotEmail', header: 'Không email', size: 110, cell: yesNoCell },
    { accessorKey: 'customerName', header: 'Khách hàng', size: 180, cell: textCell },
    { accessorKey: 'assignedUserName', header: 'Người phụ trách', size: 160, cell: textCell },
    { accessorKey: 'createdByName', header: 'Người tạo', size: 160, cell: textCell },
    { accessorKey: 'createdAt', header: 'Ngày tạo', size: 120, enableSorting: true, cell: dateCell },
    { accessorKey: 'updatedByName', header: 'Người sửa cuối', size: 160, cell: textCell },
    { accessorKey: 'updatedAt', header: 'Ngày sửa', size: 120, enableSorting: true, cell: dateCell },
];
