import { formatISODate } from '@/shared/utils/date';
import type { ExportColumn } from '@/shared/components/export/exportTypes';
import type { QuotationResult } from '../types/quotationTypes';

const STATUS_LABELS: Record<string, string> = {
    draft: 'Nháp', pending: 'Chờ duyệt', approved: 'Đã duyệt', rejected: 'Từ chối',
    sent: 'Đã gửi', accepted: 'Đã chấp nhận', expired: 'Hết hạn',
};

const RESPONSE_LABELS: Record<string, string> = {
    accepted: 'Đồng ý', adjust: 'Đề nghị chỉnh sửa', rejected: 'Từ chối',
};

// các cột khả dụng khi xuất file phân hệ Báo giá
export const quotationExportColumns: ExportColumn<QuotationResult>[] = [
    { key: 'code', label: 'Số báo giá' },
    { key: 'status', label: 'Trạng thái', format: r => STATUS_LABELS[r.status] ?? r.status },
    { key: 'customerName', label: 'Khách hàng', format: r => r.customerName ?? '' },
    { key: 'contactName', label: 'Liên hệ', format: r => r.contactName ?? '' },
    { key: 'opportunityName', label: 'Cơ hội', format: r => r.opportunityName ?? '' },
    { key: 'ownerName', label: 'Người phụ trách', format: r => r.ownerName ?? '' },
    { key: 'subtotal', label: 'Tạm tính', format: r => r.subtotal ?? '' },
    { key: 'discount', label: 'Chiết khấu', format: r => r.discount ?? '' },
    { key: 'tax', label: 'Thuế', format: r => r.tax ?? '' },
    { key: 'total', label: 'Tổng tiền', format: r => r.total ?? '' },
    { key: 'customerResponse', label: 'Phản hồi khách', format: r => (r.customerResponse ? (RESPONSE_LABELS[r.customerResponse] ?? r.customerResponse) : '') },
    { key: 'quoteDate', label: 'Ngày báo giá', format: r => r.quoteDate ? formatISODate(r.quoteDate) : '' },
    { key: 'validUntil', label: 'Hiệu lực đến', format: r => r.validUntil ? formatISODate(r.validUntil) : '' },
    { key: 'createdAt', label: 'Ngày tạo', format: r => formatISODate(r.createdAt) },
];
