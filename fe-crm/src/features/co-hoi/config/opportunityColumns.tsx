import { type ColumnDef } from '@tanstack/react-table';
import { badgeCell, currencyCell, dateCell, fkCell, textCell } from '@/shared/components/table/cells';
import type { OpportunityResult } from '../types/opportunityTypes';

const STATUS_COLORS: Record<string, string> = {
    open: 'bg-blue-100 text-blue-700',
    won: 'bg-green-100 text-green-700',
    lost: 'bg-red-100 text-red-600',
};

const STATUS_LABELS: Record<string, string> = {
    open: 'Đang mở', won: 'Đã thắng', lost: 'Đã thua',
};

/** Map ID → tên cho các cột khóa ngoại của Cơ hội. */
export interface OpportunityColumnLookups {
    customers: Map<number, string>;
    contacts: Map<number, string>;
    users: Map<number, string>;
    stages: Map<number, string>;
}

/** Tạo danh sách cột Cơ hội — hiển thị đầy đủ trường + tên khóa ngoại. */
export const getOpportunityColumns = (lk: OpportunityColumnLookups): ColumnDef<OpportunityResult>[] => [
    { accessorKey: 'code', header: 'Mã', size: 100, enableSorting: true },
    { accessorKey: 'name', header: 'Tên cơ hội', size: 200, enableSorting: true },
    { accessorKey: 'opportunityType', header: 'Loại', size: 120, cell: textCell },
    { accessorKey: 'customerId', header: 'Khách hàng', size: 180, cell: fkCell(lk.customers) },
    { accessorKey: 'contactId', header: 'Liên hệ', size: 160, cell: fkCell(lk.contacts) },
    { accessorKey: 'stageId', header: 'Giai đoạn', size: 150, cell: fkCell(lk.stages) },
    { accessorKey: 'amount', header: 'Giá trị', size: 140, cell: currencyCell },
    { accessorKey: 'expectedRevenue', header: 'Doanh thu dự kiến', size: 160, cell: currencyCell },
    {
        accessorKey: 'probability',
        header: 'Xác suất',
        size: 100,
        cell: ({ getValue }) => {
            const v = getValue<number | null>();
            return v != null ? v + '%' : '—';
        },
    },
    { accessorKey: 'status', header: 'Trạng thái', size: 120, cell: badgeCell(STATUS_LABELS, STATUS_COLORS) },
    { accessorKey: 'source', header: 'Nguồn', size: 120, cell: textCell },
    { accessorKey: 'winLossReason', header: 'Lý do thắng/thua', size: 180, cell: textCell },
    { accessorKey: 'description', header: 'Mô tả', size: 200, cell: textCell },
    { accessorKey: 'ownerId', header: 'Người phụ trách', size: 160, cell: fkCell(lk.users) },
    { accessorKey: 'expectedCloseDate', header: 'Ngày đóng dự kiến', size: 160, cell: dateCell },
    { accessorKey: 'createdAt', header: 'Ngày tạo', size: 120, enableSorting: true, cell: dateCell },
];
