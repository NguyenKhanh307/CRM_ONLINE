import { type ColumnDef } from '@tanstack/react-table';
import { formatISODate } from '@/shared/utils/date';
import type { OrderResult } from '../types/orderTypes';

const ORDER_STATUS_COLORS: Record<string, string> = {
    draft: 'bg-gray-100 text-gray-600',
    confirmed: 'bg-blue-100 text-blue-700',
    processing: 'bg-yellow-100 text-yellow-700',
    delivered: 'bg-green-100 text-green-700',
    cancelled: 'bg-red-100 text-red-600',
};

const PAYMENT_COLORS: Record<string, string> = {
    unpaid: 'bg-red-100 text-red-600',
    partial: 'bg-yellow-100 text-yellow-700',
    paid: 'bg-green-100 text-green-700',
};

const PAYMENT_LABELS: Record<string, string> = {
    unpaid: 'Chưa thanh toán', partial: 'Thanh toán một phần', paid: 'Đã thanh toán',
};

export const orderColumns: ColumnDef<OrderResult>[] = [
    { accessorKey: 'code', header: 'Mã đơn hàng', size: 140, enableSorting: true },
    {
        accessorKey: 'status',
        header: 'Trạng thái',
        size: 130,
        cell: ({ getValue }) => {
            const s = getValue<string>();
            return (
                <span className={`inline-block px-2 py-0.5 rounded text-sm font-medium ${ORDER_STATUS_COLORS[s] ?? 'bg-gray-100 text-gray-600'}`}>
                    {s}
                </span>
            );
        },
    },
    {
        accessorKey: 'paymentStatus',
        header: 'Thanh toán',
        size: 170,
        cell: ({ getValue }) => {
            const s = getValue<string>();
            return (
                <span className={`inline-block px-2 py-0.5 rounded text-sm font-medium ${PAYMENT_COLORS[s] ?? 'bg-gray-100 text-gray-600'}`}>
                    {PAYMENT_LABELS[s] ?? s}
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
    { accessorKey: 'orderDate', header: 'Ngày đặt hàng', size: 140 },
    {
        accessorKey: 'createdAt',
        header: 'Ngày tạo',
        size: 120,
        enableSorting: true,
        cell: ({ getValue }) => formatISODate(getValue<string>()),
    },
];
