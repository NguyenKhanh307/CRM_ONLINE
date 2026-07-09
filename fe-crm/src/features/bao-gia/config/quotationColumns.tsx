import { type ColumnDef } from '@tanstack/react-table';
import { badgeCell, currencyCell, dateCell, numberCell, textCell } from '@/shared/components/table/cells';
import type { QuotationResult } from '../types/quotationTypes';

const STATUS_COLORS: Record<string, string> = {
    draft: 'bg-gray-100 text-gray-600',
    pending: 'bg-yellow-100 text-yellow-700',
    approved: 'bg-green-100 text-green-700',
    rejected: 'bg-red-100 text-red-600',
    sent: 'bg-blue-100 text-blue-700',
    expired: 'bg-orange-100 text-orange-700',
};

const STATUS_LABELS: Record<string, string> = {
    draft: 'Nháp', pending: 'Chờ duyệt', approved: 'Đã duyệt',
    rejected: 'Từ chối', sent: 'Đã gửi', expired: 'Hết hạn',
};

/** Tạo danh sách cột Báo giá — hiển thị đầy đủ trường + tên khóa ngoại (do BE resolve sẵn). */
export const getQuotationColumns = (): ColumnDef<QuotationResult>[] => [
    { accessorKey: 'code', header: 'Số báo giá', size: 130, enableSorting: true },
    { accessorKey: 'customerName', header: 'Khách hàng', size: 180, cell: textCell },
    { accessorKey: 'contactName', header: 'Liên hệ', size: 160, cell: textCell },
    { accessorKey: 'opportunityName', header: 'Cơ hội', size: 160, cell: textCell },
    { accessorKey: 'ownerName', header: 'Người phụ trách', size: 160, cell: textCell },
    { accessorKey: 'status', header: 'Trạng thái', size: 120, cell: badgeCell(STATUS_LABELS, STATUS_COLORS) },
    { accessorKey: 'quoteDate', header: 'Ngày báo giá', size: 130, cell: dateCell },
    { accessorKey: 'validUntil', header: 'Hiệu lực đến', size: 130, cell: dateCell },
    { accessorKey: 'currency', header: 'Tiền tệ', size: 90, cell: textCell },
    { accessorKey: 'exchangeRate', header: 'Tỷ giá', size: 100, cell: numberCell },
    { accessorKey: 'subtotal', header: 'Tạm tính', size: 150, cell: currencyCell },
    { accessorKey: 'discount', header: 'Chiết khấu', size: 140, cell: currencyCell },
    { accessorKey: 'tax', header: 'Thuế', size: 140, cell: currencyCell },
    { accessorKey: 'total', header: 'Tổng tiền', size: 150, cell: currencyCell },
    { accessorKey: 'note', header: 'Ghi chú', size: 200, cell: textCell },
    { accessorKey: 'createdAt', header: 'Ngày tạo', size: 120, enableSorting: true, cell: dateCell },
];
