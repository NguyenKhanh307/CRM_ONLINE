import { type ColumnDef } from '@tanstack/react-table';
import { formatISODate } from '@/shared/utils/date';

export type UserRole = 'Admin' | 'Manager' | 'Staff' | 'Viewer';
export type UserStatus = 'active' | 'inactive';

export interface UserRow {
    id: number;
    fullName: string;
    email: string;
    role: UserRole;
    status: UserStatus;
    createdAt: string;
}

const ROLE_COLORS: Record<UserRole, string> = {
    Admin:   'bg-purple-100 text-purple-700',
    Manager: 'bg-blue-100 text-blue-700',
    Staff:   'bg-green-100 text-green-700',
    Viewer:  'bg-gray-100 text-gray-600',
};

/** Định nghĩa cột cho bảng Users. */
export const userColumns: ColumnDef<UserRow>[] = [
    {
        accessorKey: 'id',
        header: 'ID',
        enableSorting: true,
        size: 60,
    },
    {
        accessorKey: 'fullName',
        header: 'Họ và tên',
        enableSorting: true,
    },
    {
        accessorKey: 'email',
        header: 'Email',
        enableSorting: true,
    },
    {
        accessorKey: 'role',
        header: 'Vai trò',
        enableSorting: true,
        cell: ({ getValue }) => {
            const role = getValue<UserRole>();
            return (
                <span className={`inline-block px-2 py-0.5 rounded text-sm font-medium ${ROLE_COLORS[role]}`}>
                    {role}
                </span>
            );
        },
    },
    {
        accessorKey: 'status',
        header: 'Trạng thái',
        enableSorting: true,
        cell: ({ getValue }) => {
            const status = getValue<UserStatus>();
            return (
                <span
                    className={`inline-flex items-center gap-1 px-2 py-0.5 rounded text-sm font-medium ${
                        status === 'active'
                            ? 'bg-green-100 text-green-700'
                            : 'bg-red-100 text-red-600'
                    }`}
                >
                    <span className={`w-1.5 h-1.5 rounded-full ${status === 'active' ? 'bg-green-500' : 'bg-red-400'}`} />
                    {status === 'active' ? 'Hoạt động' : 'Vô hiệu'}
                </span>
            );
        },
    },
    {
        accessorKey: 'createdAt',
        header: 'Ngày tạo',
        enableSorting: true,
        cell: ({ getValue }) => formatISODate(getValue<string>()),
    },
];
