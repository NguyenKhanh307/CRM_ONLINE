import { formatISODate } from '@/shared/utils/date';
import type { ExportColumn } from '@/shared/components/export/exportTypes';
import type { ContactResult } from '../types/contactTypes';

/** Các cột khả dụng khi xuất file phân hệ Liên hệ. */
export const contactExportColumns: ExportColumn<ContactResult>[] = [
    { key: 'fullName', label: 'Họ và tên' },
    { key: 'email', label: 'Email' },
    { key: 'isPrimary', label: 'Liên hệ chính', format: r => (r.isPrimary ? 'Chính' : 'Phụ') },
    { key: 'createdAt', label: 'Ngày tạo', format: r => formatISODate(r.createdAt) },
];
