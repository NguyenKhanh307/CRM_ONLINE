import { type ColumnDef } from '@tanstack/react-table';
import { badgeCell, currencyCell, dateCell, numberCell, textCell, yesNoCell } from '@/shared/components/table/cells';
import type { OrderResult } from '../types/orderTypes';

const ORDER_STATUS_COLORS: Record<string, string> = {
    draft: 'bg-gray-100 text-gray-600',
    confirmed: 'bg-blue-100 text-blue-700',
    processing: 'bg-yellow-100 text-yellow-700',
    completed: 'bg-green-100 text-green-700',
    cancelled: 'bg-red-100 text-red-600',
};

const ORDER_STATUS_LABELS: Record<string, string> = {
    draft: 'Nháp', confirmed: 'Đã xác nhận', processing: 'Đang xử lý',
    completed: 'Hoàn tất', cancelled: 'Đã hủy',
};

/** Tạo danh sách cột Đơn hàng — hiển thị đầy đủ trường + tên khóa ngoại (do BE resolve sẵn). */
export const getOrderColumns = (): ColumnDef<OrderResult>[] => [
    { accessorKey: 'code', header: 'Mã Đơn hàng', size: 140, enableSorting: true },
    { accessorKey: 'customerName', header: 'Khách hàng', size: 180, cell: textCell },
    { accessorKey: 'contactName', header: 'Liên hệ', size: 160, cell: textCell },
    { accessorKey: 'quotationCode', header: 'Báo giá', size: 140, cell: textCell },
    { accessorKey: 'opportunityName', header: 'Cơ hội', size: 160, cell: textCell },
    { accessorKey: 'campaignName', header: 'Chiến dịch', size: 160, cell: textCell },
    { accessorKey: 'ownerName', header: 'Người phụ trách', size: 160, cell: textCell },
    { accessorKey: 'status', header: 'Trạng thái', size: 130, cell: badgeCell(ORDER_STATUS_LABELS, ORDER_STATUS_COLORS) },
    { accessorKey: 'isLocked', header: 'Đã khóa', size: 100, cell: yesNoCell },
    { accessorKey: 'orderDate', header: 'Ngày đơn hàng', size: 140, cell: dateCell },
    { accessorKey: 'deliveryDate', header: 'Ngày giao', size: 140, cell: dateCell },
    { accessorKey: 'currency', header: 'Tiền tệ', size: 90, cell: textCell },
    { accessorKey: 'exchangeRate', header: 'Tỷ giá', size: 100, cell: numberCell },
    { accessorKey: 'billingAddress', header: 'Địa chỉ xuất HĐ', size: 180, cell: textCell },
    { accessorKey: 'taxCode', header: 'Mã số thuế', size: 130, cell: textCell },
    { accessorKey: 'subtotal', header: 'Tạm tính', size: 150, cell: currencyCell },
    { accessorKey: 'discount', header: 'Chiết khấu', size: 140, cell: currencyCell },
    { accessorKey: 'tax', header: 'Thuế', size: 140, cell: currencyCell },
    { accessorKey: 'total', header: 'Tổng tiền', size: 150, cell: currencyCell },
    { accessorKey: 'note', header: 'Ghi chú', size: 200, cell: textCell },
    { accessorKey: 'createdAt', header: 'Ngày tạo', size: 120, enableSorting: true, cell: dateCell },
];
