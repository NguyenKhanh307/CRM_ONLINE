import { formatISODate } from '@/shared/utils/date';
import type { ExportColumn } from '@/shared/components/export/exportTypes';
import type { OrderResult } from '../types/orderTypes';

const ORDER_STATUS_LABELS: Record<string, string> = {
    draft: 'Nháp', confirmed: 'Đã xác nhận', processing: 'Đang xử lý',
    completed: 'Hoàn tất', cancelled: 'Đã hủy',
};

/** Các cột khả dụng khi xuất file phân hệ Đơn hàng. */
export const orderExportColumns: ExportColumn<OrderResult>[] = [
    { key: 'code', label: 'Mã Đơn hàng' },
    { key: 'status', label: 'Trạng thái', format: r => ORDER_STATUS_LABELS[r.status] ?? r.status },
    { key: 'total', label: 'Tổng tiền', format: r => r.total ?? '' },
    { key: 'orderDate', label: 'Ngày đơn hàng', format: r => r.orderDate ? formatISODate(r.orderDate) : '' },
    { key: 'deliveryDate', label: 'Ngày giao', format: r => r.deliveryDate ? formatISODate(r.deliveryDate) : '' },
    { key: 'createdAt', label: 'Ngày tạo', format: r => formatISODate(r.createdAt) },
];
