import { type ColumnDef } from '@tanstack/react-table';
import { badgeCell, currencyCell, dateCell, fkCell, numberCell, textCell, yesNoCell } from '@/shared/components/table/cells';
import type { InvoiceResult } from '../types/invoiceTypes';

const INVOICE_STATUS_COLORS: Record<string, string> = {
    draft: 'bg-gray-100 text-gray-600',
    sent: 'bg-blue-100 text-blue-700',
    partially_paid: 'bg-yellow-100 text-yellow-700',
    paid: 'bg-green-100 text-green-700',
    cancelled: 'bg-red-100 text-red-600',
};

const INVOICE_STATUS_LABELS: Record<string, string> = {
    draft: 'Nháp', sent: 'Đã gửi', partially_paid: 'Thanh toán một phần',
    paid: 'Đã thanh toán', cancelled: 'Đã hủy',
};

const PAYMENT_COLORS: Record<string, string> = {
    unpaid: 'bg-red-100 text-red-600',
    partial: 'bg-yellow-100 text-yellow-700',
    paid: 'bg-green-100 text-green-700',
};

const PAYMENT_LABELS: Record<string, string> = {
    unpaid: 'Chưa thanh toán', partial: 'Thanh toán một phần', paid: 'Đã thanh toán',
};

/** Map ID → tên cho các cột khóa ngoại của Hóa đơn. */
export interface InvoiceColumnLookups {
    customers: Map<number, string>;
    contacts: Map<number, string>;
    quotations: Map<number, string>;
    opportunities: Map<number, string>;
    users: Map<number, string>;
    orgUnits: Map<number, string>;
    invoices: Map<number, string>;
}

/** Tạo danh sách cột Hóa đơn — hiển thị đầy đủ trường + tên khóa ngoại. */
export const getInvoiceColumns = (lk: InvoiceColumnLookups): ColumnDef<InvoiceResult>[] => [
    { accessorKey: 'code', header: 'Mã Hóa đơn', size: 140, enableSorting: true },
    { accessorKey: 'customerId', header: 'Khách hàng', size: 180, cell: fkCell(lk.customers) },
    { accessorKey: 'contactId', header: 'Liên hệ', size: 160, cell: fkCell(lk.contacts) },
    { accessorKey: 'quotationId', header: 'Báo giá', size: 140, cell: fkCell(lk.quotations) },
    { accessorKey: 'opportunityId', header: 'Cơ hội', size: 160, cell: fkCell(lk.opportunities) },
    { accessorKey: 'ownerId', header: 'Người phụ trách', size: 160, cell: fkCell(lk.users) },
    { accessorKey: 'status', header: 'Trạng thái', size: 130, cell: badgeCell(INVOICE_STATUS_LABELS, INVOICE_STATUS_COLORS) },
    { accessorKey: 'paymentStatus', header: 'Thanh toán', size: 170, cell: badgeCell(PAYMENT_LABELS, PAYMENT_COLORS) },
    { accessorKey: 'isLocked', header: 'Đã khóa', size: 100, cell: yesNoCell },
    { accessorKey: 'invoiceDate', header: 'Ngày hóa đơn', size: 140, cell: dateCell },
    { accessorKey: 'dueDate', header: 'Hạn thanh toán', size: 140, cell: dateCell },
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
