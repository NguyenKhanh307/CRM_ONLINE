import { type ColumnDef } from '@tanstack/react-table';
import { badgeCell, dateCell, textCell } from '@/shared/components/table/cells';
import type { TicketResult } from '../types/ticketTypes';
import {
    TYPE_LABELS, TYPE_COLORS, STATUS_LABELS, STATUS_COLORS,
    PRIORITY_LABELS, PRIORITY_COLORS, CHANNEL_LABELS,
} from './ticketEnums';

/** Cell cờ quá hạn SLA: badge đỏ khi isOverdue. */
function overdueCell({ row }: { row: { original: TicketResult } }) {
    return row.original.isOverdue
        ? <span className="inline-block px-2 py-0.5 rounded text-sm font-medium bg-red-100 text-red-600">Quá hạn</span>
        : <span className="text-gray-400">—</span>;
}

/** Tạo danh sách cột phiếu hỗ trợ — hiển thị đầy đủ trường + tên khóa ngoại (do BE resolve sẵn). */
export const getTicketColumns = (): ColumnDef<TicketResult>[] => [
    { accessorKey: 'code', header: 'Mã phiếu', size: 120, enableSorting: true },
    { accessorKey: 'subject', header: 'Tiêu đề', size: 220, cell: textCell },
    { accessorKey: 'type', header: 'Loại', size: 110, cell: badgeCell(TYPE_LABELS, TYPE_COLORS) },
    { accessorKey: 'status', header: 'Trạng thái', size: 130, cell: badgeCell(STATUS_LABELS, STATUS_COLORS) },
    { accessorKey: 'priority', header: 'Ưu tiên', size: 100, cell: badgeCell(PRIORITY_LABELS, PRIORITY_COLORS) },
    { id: 'overdue', header: 'SLA', size: 100, enableSorting: false, cell: overdueCell },
    { accessorKey: 'customerName', header: 'Khách hàng', size: 180, cell: textCell },
    { accessorKey: 'contactName', header: 'Liên hệ', size: 160, cell: textCell },
    { accessorKey: 'assignedUserName', header: 'Người xử lý', size: 160, cell: textCell },
    { accessorKey: 'channel', header: 'Kênh', size: 110, cell: ({ getValue }) => CHANNEL_LABELS[getValue() as string] ?? '—' },
    { accessorKey: 'slaDueAt', header: 'Hạn SLA', size: 150, cell: dateCell },
    { accessorKey: 'satisfactionScore', header: 'CSAT', size: 90, cell: ({ getValue }) => {
        const v = getValue() as number | null;
        return v != null ? `${v}/5` : '—';
    } },
    { accessorKey: 'createdAt', header: 'Ngày tạo', size: 120, enableSorting: true, cell: dateCell },
];
