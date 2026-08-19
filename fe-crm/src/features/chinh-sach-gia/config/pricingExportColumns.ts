import { formatISODate } from '@/shared/utils/date';
import type { ExportColumn } from '@/shared/components/export/exportTypes';
import type { PricePolicyResult } from '../types/pricingTypes';

const STATUS_LABELS: Record<string, string> = {
    active: 'Đang áp dụng', inactive: 'Ngừng áp dụng', expired: 'Hết hạn',
};

const TYPE_LABELS: Record<string, string> = {
    standard: 'Tiêu chuẩn', promotional: 'Khuyến mãi', special: 'Đặc biệt',
};

// các cột khả dụng khi xuất file phân hệ Chính sách giá
export const pricingExportColumns: ExportColumn<PricePolicyResult>[] = [
    { key: 'code', label: 'Mã chính sách' },
    { key: 'name', label: 'Tên chính sách' },
    { key: 'type', label: 'Loại', format: r => r.type ? (TYPE_LABELS[r.type] ?? r.type) : '' },
    { key: 'priority', label: 'Độ ưu tiên', format: r => r.priority ?? '' },
    { key: 'startDate', label: 'Ngày bắt đầu', format: r => r.startDate ? formatISODate(r.startDate) : '' },
    { key: 'endDate', label: 'Ngày kết thúc', format: r => r.endDate ? formatISODate(r.endDate) : '' },
    { key: 'status', label: 'Trạng thái', format: r => STATUS_LABELS[r.status] ?? r.status },
    { key: 'createdAt', label: 'Ngày tạo', format: r => formatISODate(r.createdAt) },
];
