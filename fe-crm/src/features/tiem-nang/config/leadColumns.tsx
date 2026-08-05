import { type ColumnDef } from '@tanstack/react-table';
import { badgeCell, dateCell, numberCell, textCell } from '@/shared/components/table/cells';
import type { LeadResult } from '../types/leadTypes';

const STATUS_COLORS: Record<string, string> = {
    new:        'bg-gray-100 text-gray-600',
    contacting: 'bg-blue-100 text-blue-700',
    converted:  'bg-emerald-100 text-emerald-700',
};

const STATUS_LABELS: Record<string, string> = {
    new: 'Mới', contacting: 'Đang liên hệ', converted: 'Đã chuyển đổi',
};

// tạo danh sách cột Tiềm năng — hiển thị đầy đủ trường + tên khóa ngoại (do BE resolve sẵn)
export const getLeadColumns = (): ColumnDef<LeadResult>[] => [
    { accessorKey: 'code', header: 'Mã', size: 100, enableSorting: true },
    { accessorKey: 'name', header: 'Tên tiềm năng', size: 180, enableSorting: true },
    { accessorKey: 'companyName', header: 'Công ty', size: 180, cell: textCell },
    { accessorKey: 'leadType', header: 'Loại', size: 120, cell: textCell },
    { accessorKey: 'phone', header: 'Điện thoại', size: 130, cell: textCell },
    { accessorKey: 'email', header: 'Email', size: 180, enableSorting: true, cell: textCell },
    { accessorKey: 'taxCode', header: 'Mã số thuế', size: 130, cell: textCell },
    { accessorKey: 'website', header: 'Website', size: 160, cell: textCell },
    { accessorKey: 'industry', header: 'Ngành nghề', size: 150, cell: textCell },
    { accessorKey: 'source', header: 'Nguồn', size: 120, cell: textCell },
    {
        accessorKey: 'status',
        header: 'Trạng thái',
        size: 140,
        cell: badgeCell(STATUS_LABELS, STATUS_COLORS),
    },
    { accessorKey: 'score', header: 'Điểm', size: 90, enableSorting: true, cell: numberCell },
    { accessorKey: 'ownerName', header: 'Người phụ trách', size: 160, cell: textCell },
    { accessorKey: 'contactName', header: 'Liên hệ', size: 160, cell: textCell },
    { accessorKey: 'campaignName', header: 'Chiến dịch', size: 160, cell: textCell },
    { accessorKey: 'note', header: 'Ghi chú', size: 200, cell: textCell },
    { accessorKey: 'createdByName', header: 'Người tạo', size: 160, cell: textCell },
    { accessorKey: 'createdAt', header: 'Ngày tạo', size: 120, enableSorting: true, cell: dateCell },
    { accessorKey: 'updatedByName', header: 'Người sửa cuối', size: 160, cell: textCell },
    { accessorKey: 'updatedAt', header: 'Ngày sửa', size: 120, enableSorting: true, cell: dateCell },
];
