import { formatISODate } from '@/shared/utils/date';
import type { ExportColumn } from '@/shared/components/export/exportTypes';
import type { OrderResult } from '../types/orderTypes';

const PAYMENT_LABELS: Record<string, string> = {
    unpaid: 'Chưa thanh toán', partial: 'Thanh toán một phần', paid: 'Đã thanh toán',
};

/** Các cột khả dụng khi xuất file phân hệ Đơn hàng. */
export const orderExportColumns: ExportColumn<OrderResult>[] = [
    { key: 'code', label: 'Mã đơn hàng' },
    { key: 'status', label: 'Trạng thái' },
    { key: 'paymentStatus', label: 'Thanh toán', format: r => PAYMENT_LABELS[r.paymentStatus] ?? r.paymentStatus },
    { key: 'total', label: 'Tổng tiền', format: r => r.total ?? '' },
    { key: 'orderDate', label: 'Ngày đặt hàng' },
    { key: 'createdAt', label: 'Ngày tạo', format: r => formatISODate(r.createdAt) },
];
