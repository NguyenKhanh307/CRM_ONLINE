import { type ColumnDef } from '@tanstack/react-table';

/** Kiểu dữ liệu mẫu — thay thế bằng interface thực của feature. */
export interface ExampleRow {
    id: number;
    name: string;
    status: string;
    createdAt: string;
}

/**
 * Định nghĩa cột cho bảng ví dụ.
 * Sao chép file này vào features/<ten-feature>/config/<ten-feature>Columns.tsx
 * và thay ExampleRow bằng kiểu dữ liệu thực.
 */
export const exampleColumns: ColumnDef<ExampleRow>[] = [
    {
        accessorKey: 'id',
        header: 'ID',
        enableSorting: true,
        size: 60,
    },
    {
        accessorKey: 'name',
        header: 'Tên',
        enableSorting: true,
    },
    {
        accessorKey: 'status',
        header: 'Trạng thái',
        enableSorting: true,
    },
    {
        accessorKey: 'createdAt',
        header: 'Ngày tạo',
        enableSorting: true,
    },
];
