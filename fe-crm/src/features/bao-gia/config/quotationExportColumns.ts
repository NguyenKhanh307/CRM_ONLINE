import { formatISODate } from '@/shared/utils/date';
import type { ExportColumn } from '@/shared/components/export/exportTypes';
import type { QuotationResult } from '../types/quotationTypes';

const STATUS_LABELS: Record<string, string> = {
    draft: 'Nháp', sent: 'Đã gửi', approved: 'Đã duyệt',
    rejected: 'Từ chối', expired: 'Hết hạn',
};

/** Các cột khả dụng khi xuất file phân hệ Báo giá. */
export const quotationExportColumns: ExportColumn<QuotationResult>[] = [
    { key: 'code', label: 'Số báo giá' },
    { key: 'status', label: 'Trạng thái', format: r => STATUS_LABELS[r.status] ?? r.status },
    { key: 'total', label: 'Tổng tiền', format: r => r.total ?? '' },
    { key: 'quoteDate', label: 'Ngày báo giá' },
    { key: 'validUntil', label: 'Hiệu lực đến' },
    { key: 'createdAt', label: 'Ngày tạo', format: r => formatISODate(r.createdAt) },
];
