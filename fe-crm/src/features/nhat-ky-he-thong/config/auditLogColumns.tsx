import { type ColumnDef } from '@tanstack/react-table';
import { formatISODate } from '@/shared/utils/date';
import type { AuditLogEntry } from '../types/auditLogTypes';

// định dạng ngày giờ đầy đủ (dd/mm/yyyy HH:mm) từ chuỗi ISO
function formatDateTime(iso: string): string {
    if (!iso) return '';
    const time = new Date(iso).toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' });
    return `${formatISODate(iso)} ${time}`;
}

export const auditLogColumns: ColumnDef<AuditLogEntry>[] = [
    { accessorKey: 'actorName', header: 'Người thực hiện', size: 160,
        cell: ({ getValue }) => getValue<string | null>() ?? '—' },
    { accessorKey: 'action', header: 'Hành động', size: 220 },
    { accessorKey: 'targetLabel', header: 'Bản ghi', size: 200,
        cell: ({ getValue }) => getValue<string | null>() ?? '—' },
    { accessorKey: 'note', header: 'Ghi chú', cell: ({ getValue }) => getValue<string | null>() ?? '—' },
    { accessorKey: 'occurredAt', header: 'Thời điểm', size: 160,
        enableSorting: true, cell: ({ getValue }) => formatDateTime(getValue<string>()) },
];
