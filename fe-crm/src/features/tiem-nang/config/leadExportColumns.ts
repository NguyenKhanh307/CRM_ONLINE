import { formatISODate } from '@/shared/utils/date';
import type { ExportColumn } from '@/shared/components/export/exportTypes';
import type { LeadResult } from '../types/leadTypes';

const STATUS_LABELS: Record<string, string> = {
    new: 'Mới', contacting: 'Đang liên hệ', qualified: 'Đủ điều kiện',
    converted: 'Đã chuyển đổi', lost: 'Thất bại',
};

// các cột khả dụng khi xuất file phân hệ Tiềm năng
export const leadExportColumns: ExportColumn<LeadResult>[] = [
    { key: 'code', label: 'Mã' },
    { key: 'name', label: 'Tên tiềm năng' },
    { key: 'phone', label: 'Điện thoại' },
    { key: 'email', label: 'Email' },
    { key: 'source', label: 'Nguồn' },
    { key: 'status', label: 'Trạng thái', format: r => STATUS_LABELS[r.status] ?? r.status },
    { key: 'estimatedValue', label: 'Giá trị dự kiến', format: r => r.estimatedValue ?? '' },
    { key: 'createdAt', label: 'Ngày tạo', format: r => formatISODate(r.createdAt) },
];
