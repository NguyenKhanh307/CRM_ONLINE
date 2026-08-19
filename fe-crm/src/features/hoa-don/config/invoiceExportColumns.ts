import { formatISODate } from '@/shared/utils/date';
import type { ExportColumn } from '@/shared/components/export/exportTypes';
import type { InvoiceResult } from '../types/invoiceTypes';

const STATUS_LABELS: Record<string, string> = {
    draft: 'Nháp', sent: 'Đã gửi', partially_paid: 'Thanh toán một phần', paid: 'Đã thanh toán', cancelled: 'Đã hủy',
};

const PAYMENT_LABELS: Record<string, string> = {
    unpaid: 'Chưa thanh toán', partial: 'Thanh toán một phần', paid: 'Đã thanh toán',
};

// các cột khả dụng khi xuất file phân hệ Hóa đơn
export const invoiceExportColumns: ExportColumn<InvoiceResult>[] = [
    { key: 'code', label: 'Mã Hóa đơn' },
    { key: 'status', label: 'Trạng thái', format: r => STATUS_LABELS[r.status] ?? r.status },
    { key: 'paymentStatus', label: 'Thanh toán', format: r => PAYMENT_LABELS[r.paymentStatus] ?? r.paymentStatus },
    { key: 'orderCode', label: 'Đơn hàng', format: r => r.orderCode ?? '' },
    { key: 'ownerName', label: 'Người phụ trách', format: r => r.ownerName ?? '' },
    { key: 'subtotal', label: 'Tạm tính', format: r => r.subtotal ?? '' },
    { key: 'discount', label: 'Chiết khấu', format: r => r.discount ?? '' },
    { key: 'tax', label: 'Thuế', format: r => r.tax ?? '' },
    { key: 'total', label: 'Tổng tiền', format: r => r.total ?? '' },
    { key: 'invoiceDate', label: 'Ngày hóa đơn', format: r => r.invoiceDate ? formatISODate(r.invoiceDate) : '' },
    { key: 'dueDate', label: 'Hạn thanh toán', format: r => r.dueDate ? formatISODate(r.dueDate) : '' },
    { key: 'createdAt', label: 'Ngày tạo', format: r => formatISODate(r.createdAt) },
];
