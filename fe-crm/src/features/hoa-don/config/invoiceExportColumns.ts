import { formatISODate } from '@/shared/utils/date';
import type { ExportColumn } from '@/shared/components/export/exportTypes';
import type { InvoiceResult } from '../types/invoiceTypes';

const PAYMENT_LABELS: Record<string, string> = {
    unpaid: 'Chưa thanh toán', partial: 'Thanh toán một phần', paid: 'Đã thanh toán',
};

/** Các cột khả dụng khi xuất file phân hệ Hóa đơn. */
export const invoiceExportColumns: ExportColumn<InvoiceResult>[] = [
    { key: 'code', label: 'Mã Hóa đơn' },
    { key: 'status', label: 'Trạng thái' },
    { key: 'paymentStatus', label: 'Thanh toán', format: r => PAYMENT_LABELS[r.paymentStatus] ?? r.paymentStatus },
    { key: 'total', label: 'Tổng tiền', format: r => r.total ?? '' },
    { key: 'invoiceDate', label: 'Ngày hóa đơn' },
    { key: 'orderCode', label: 'Đơn hàng', format: r => r.orderCode ?? '' },
    { key: 'campaignName', label: 'Chiến dịch', format: r => r.campaignName ?? '' },
    { key: 'createdAt', label: 'Ngày tạo', format: r => formatISODate(r.createdAt) },
];
