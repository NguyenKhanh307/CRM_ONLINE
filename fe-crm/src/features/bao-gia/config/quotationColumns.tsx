import { type ColumnDef } from '@tanstack/react-table';
import { formatISODate } from '@/shared/utils/date';
import type { QuotationResult } from '../types/quotationTypes';

const STATUS_COLORS: Record<string, string> = {
    draft: 'bg-gray-100 text-gray-600',
    sent: 'bg-blue-100 text-blue-700',
    approved: 'bg-green-100 text-green-700',
    rejected: 'bg-red-100 text-red-600',
    expired: 'bg-orange-100 text-orange-700',
};

const STATUS_LABELS: Record<string, string> = {
    draft: 'Nháp', sent: 'Đã gửi', approved: 'Đã duyệt',
    rejected: 'Từ chối', expired: 'Hết hạn',
};

export const quotationColumns: ColumnDef<QuotationResult>[] = [
    { accessorKey: 'code', header: 'Số báo giá', size: 130, enableSorting: true },
    {
        accessorKey: 'status',
        header: 'Trạng thái',
        size: 120,
        cell: ({ getValue }) => {
            const s = getValue<string>();
            return (
                <span className={`inline-block px-2 py-0.5 rounded text-sm font-medium ${STATUS_COLORS[s] ?? 'bg-gray-100 text-gray-600'}`}>
                    {STATUS_LABELS[s] ?? s}
                </span>
            );
        },
    },
    {
        accessorKey: 'total',
        header: 'Tổng tiền',
        size: 150,
        cell: ({ getValue }) => {
            const v = getValue<number | null>();
            return v != null ? v.toLocaleString('vi-VN') + ' đ' : '—';
        },
    },
    { accessorKey: 'quoteDate', header: 'Ngày báo giá', size: 130 },
    { accessorKey: 'validUntil', header: 'Hiệu lực đến', size: 130 },
    {
        accessorKey: 'createdAt',
        header: 'Ngày tạo',
        size: 120,
        enableSorting: true,
        cell: ({ getValue }) => formatISODate(getValue<string>()),
    },
];
