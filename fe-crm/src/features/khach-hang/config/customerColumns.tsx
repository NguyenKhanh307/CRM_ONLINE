import { type ColumnDef } from '@tanstack/react-table';
import { badgeCell, currencyCell, dateCell, labelCell, numberCell, textCell, yesNoCell } from '@/shared/components/table/cells';
import type { CustomerResult } from '../types/customerTypes';

const TYPE_LABELS: Record<string, string> = {
    individual: 'Cá nhân', company: 'Công ty',
};

const STATUS_COLORS: Record<string, string> = {
    active: 'bg-green-100 text-green-700',
    inactive: 'bg-red-100 text-red-600',
    potential: 'bg-blue-100 text-blue-700',
};

const STATUS_LABELS: Record<string, string> = {
    active: 'Hoạt động', inactive: 'Không hoạt động', potential: 'Tiềm năng',
};

// tạo danh sách cột Khách hàng — hiển thị đầy đủ trường + tên khóa ngoại (do BE resolve sẵn)
export const getCustomerColumns = (): ColumnDef<CustomerResult>[] => [
    { accessorKey: 'code', header: 'Mã KH', size: 100, enableSorting: true },
    { accessorKey: 'name', header: 'Tên khách hàng', size: 200, enableSorting: true },
    { accessorKey: 'shortName', header: 'Tên viết tắt', size: 140, cell: textCell },
    { accessorKey: 'type', header: 'Loại', size: 100, cell: labelCell(TYPE_LABELS) },
    { accessorKey: 'taxCode', header: 'Mã số thuế', size: 130, cell: textCell },
    { accessorKey: 'phone', header: 'Điện thoại', size: 130, cell: textCell },
    { accessorKey: 'email', header: 'Email', size: 180, enableSorting: true, cell: textCell },
    { accessorKey: 'website', header: 'Website', size: 160, cell: textCell },
    { accessorKey: 'address', header: 'Địa chỉ', size: 200, cell: textCell },
    { accessorKey: 'industry', header: 'Ngành nghề', size: 150, cell: textCell },
    { accessorKey: 'source', header: 'Nguồn', size: 120, cell: textCell },
    { accessorKey: 'status', header: 'Trạng thái', size: 130, cell: badgeCell(STATUS_LABELS, STATUS_COLORS) },
    { accessorKey: 'creditDays', header: 'Số ngày nợ', size: 110, cell: numberCell },
    { accessorKey: 'creditLimit', header: 'Hạn mức nợ', size: 150, cell: currencyCell },
    { accessorKey: 'rating', header: 'Xếp hạng', size: 100, cell: textCell },
    { accessorKey: 'employeeSize', header: 'Quy mô NV', size: 120, cell: textCell },
    { accessorKey: 'isDistributor', header: 'Nhà phân phối', size: 120, cell: yesNoCell },
    { accessorKey: 'ownerName', header: 'Người phụ trách', size: 160, cell: textCell },
    { accessorKey: 'createdByName', header: 'Người tạo', size: 160, cell: textCell },
    { accessorKey: 'createdAt', header: 'Ngày tạo', size: 120, enableSorting: true, cell: dateCell },
    { accessorKey: 'updatedByName', header: 'Người sửa cuối', size: 160, cell: textCell },
    { accessorKey: 'updatedAt', header: 'Ngày sửa', size: 120, enableSorting: true, cell: dateCell },
];
