import { formatISODate } from '@/shared/utils/date';
import type { ExportColumn } from '@/shared/components/export/exportTypes';
import type { LeadResult } from '../types/leadTypes';

const STATUS_LABELS: Record<string, string> = {
    new: 'Mới', contacting: 'Đang liên hệ', converted: 'Đã chuyển đổi',
};

// các cột khả dụng khi xuất file phân hệ Tiềm năng
export const leadExportColumns: ExportColumn<LeadResult>[] = [
    { key: 'code', label: 'Mã' },
    { key: 'name', label: 'Tên tiềm năng' },
    { key: 'phone', label: 'Điện thoại' },
    { key: 'email', label: 'Email' },
    { key: 'source', label: 'Nguồn' },
    { key: 'status', label: 'Trạng thái', format: r => STATUS_LABELS[r.status] ?? r.status },
    { key: 'companyName', label: 'Tên tổ chức', format: r => r.companyName ?? '' },
    { key: 'taxCode', label: 'Mã số thuế', format: r => r.taxCode ?? '' },
    { key: 'website', label: 'Website', format: r => r.website ?? '' },
    { key: 'industry', label: 'Ngành nghề', format: r => r.industry ?? '' },
    { key: 'score', label: 'Điểm', format: r => r.score ?? '' },
    { key: 'ownerName', label: 'Người phụ trách', format: r => r.ownerName ?? '' },
    { key: 'contactName', label: 'Liên hệ', format: r => r.contactName ?? '' },
    { key: 'campaignName', label: 'Chiến dịch', format: r => r.campaignName ?? '' },
    { key: 'note', label: 'Ghi chú', format: r => r.note ?? '' },
    { key: 'createdAt', label: 'Ngày tạo', format: r => formatISODate(r.createdAt) },
];
