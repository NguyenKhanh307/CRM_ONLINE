import { type ColumnDef } from '@tanstack/react-table';
import { badgeCell, currencyCell, dateCell, fkCell, numberCell, textCell, yesNoCell } from '@/shared/components/table/cells';
import type { OrderResult } from '../types/orderTypes';

const ORDER_STATUS_COLORS: Record<string, string> = {
    draft: 'bg-gray-100 text-gray-600',
    confirmed: 'bg-blue-100 text-blue-700',
    processing: 'bg-yellow-100 text-yellow-700',
    delivered: 'bg-green-100 text-green-700',
    cancelled: 'bg-red-100 text-red-600',
};

const ORDER_STATUS_LABELS: Record<string, string> = {
    draft: 'Nháp', confirmed: 'Đã xác nhận', processing: 'Đang xử lý',
    delivered: 'Đã giao', cancelled: 'Đã hủy',
};

const PAYMENT_COLORS: Record<string, string> = {
    unpaid: 'bg-red-100 text-red-600',
    partial: 'bg-yellow-100 text-yellow-700',
    paid: 'bg-green-100 text-green-700',
};

const PAYMENT_LABELS: Record<string, string> = {
    unpaid: 'Chưa thanh toán', partial: 'Thanh toán một phần', paid: 'Đã thanh toán',
};

/** Map ID → tên cho các cột khóa ngoại của Đơn hàng. */
export interface OrderColumnLookups {
    customers: Map<number, string>;
    contacts: Map<number, string>;
    quotations: Map<number, string>;
    opportunities: Map<number, string>;
    users: Map<number, string>;
    orgUnits: Map<number, string>;
    orders: Map<number, string>;
}

/** Tạo danh sách cột Đơn hàng — hiển thị đầy đủ trường + tên khóa ngoại. */
export const getOrderColumns = (lk: OrderColumnLookups): ColumnDef<OrderResult>[] => [
    { accessorKey: 'code', header: 'Mã đơn hàng', size: 140, enableSorting: true },
    { accessorKey: 'orderType', header: 'Loại đơn', size: 120, cell: textCell },
    { accessorKey: 'customerId', header: 'Khách hàng', size: 180, cell: fkCell(lk.customers) },
    { accessorKey: 'contactId', header: 'Liên hệ', size: 160, cell: fkCell(lk.contacts) },
    { accessorKey: 'quotationId', header: 'Báo giá', size: 140, cell: fkCell(lk.quotations) },
    { accessorKey: 'opportunityId', header: 'Cơ hội', size: 160, cell: fkCell(lk.opportunities) },
    { accessorKey: 'ownerId', header: 'Người phụ trách', size: 160, cell: fkCell(lk.users) },
    { accessorKey: 'executorUnitId', header: 'Đơn vị thực hiện', size: 160, cell: fkCell(lk.orgUnits) },
    { accessorKey: 'parentOrderId', header: 'Đơn hàng cha', size: 140, cell: fkCell(lk.orders) },
    { accessorKey: 'status', header: 'Trạng thái', size: 130, cell: badgeCell(ORDER_STATUS_LABELS, ORDER_STATUS_COLORS) },
    { accessorKey: 'paymentStatus', header: 'Thanh toán', size: 170, cell: badgeCell(PAYMENT_LABELS, PAYMENT_COLORS) },
    { accessorKey: 'orderDate', header: 'Ngày đặt hàng', size: 140, cell: dateCell },
    { accessorKey: 'currency', header: 'Tiền tệ', size: 90, cell: textCell },
    { accessorKey: 'exchangeRate', header: 'Tỷ giá', size: 100, cell: numberCell },
    { accessorKey: 'creditDays', header: 'Số ngày nợ', size: 110, cell: numberCell },
    { accessorKey: 'paymentDueDate', header: 'Hạn thanh toán', size: 140, cell: dateCell },
    { accessorKey: 'isInvoiced', header: 'Đã xuất hóa đơn', size: 130, cell: yesNoCell },
    { accessorKey: 'receiverName', header: 'Người nhận', size: 150, cell: textCell },
    { accessorKey: 'receiverPhone', header: 'SĐT người nhận', size: 140, cell: textCell },
    { accessorKey: 'subtotal', header: 'Tạm tính', size: 150, cell: currencyCell },
    { accessorKey: 'discount', header: 'Chiết khấu', size: 140, cell: currencyCell },
    { accessorKey: 'tax', header: 'Thuế', size: 140, cell: currencyCell },
    { accessorKey: 'total', header: 'Tổng tiền', size: 150, cell: currencyCell },
    { accessorKey: 'note', header: 'Ghi chú', size: 200, cell: textCell },
    { accessorKey: 'createdAt', header: 'Ngày tạo', size: 120, enableSorting: true, cell: dateCell },
];
