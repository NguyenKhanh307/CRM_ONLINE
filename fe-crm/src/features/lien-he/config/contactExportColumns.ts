import { formatISODate } from '@/shared/utils/date';
import type { ExportColumn } from '@/shared/components/export/exportTypes';
import type { ContactResult } from '../types/contactTypes';

const GENDER_LABELS: Record<string, string> = {
    male: 'Nam', female: 'Nữ', other: 'Khác',
};

/** Các cột khả dụng khi xuất file phân hệ Liên hệ. */
export const contactExportColumns: ExportColumn<ContactResult>[] = [
    { key: 'fullName', label: 'Họ và tên' },
    { key: 'salutation', label: 'Xưng hô', format: r => r.salutation ?? '' },
    { key: 'customerName', label: 'Tổ chức', format: r => r.customerName ?? '' },
    { key: 'title', label: 'Chức danh', format: r => r.title ?? '' },
    { key: 'department', label: 'Phòng ban', format: r => r.department ?? '' },
    { key: 'email', label: 'Email' },
    { key: 'phone', label: 'Số điện thoại', format: r => r.phone ?? '' },
    { key: 'zalo', label: 'Zalo', format: r => r.zalo ?? '' },
    { key: 'source', label: 'Nguồn gốc', format: r => r.source ?? '' },
    { key: 'gender', label: 'Giới tính', format: r => (r.gender ? (GENDER_LABELS[r.gender] ?? r.gender) : '') },
    { key: 'dateOfBirth', label: 'Ngày sinh', format: r => (r.dateOfBirth ? formatISODate(r.dateOfBirth) : '') },
    { key: 'isPrimary', label: 'Liên hệ chính', format: r => (r.isPrimary ? 'Chính' : 'Phụ') },
    { key: 'assignedUserName', label: 'Người phụ trách', format: r => r.assignedUserName ?? '' },
    { key: 'createdAt', label: 'Ngày tạo', format: r => formatISODate(r.createdAt) },
];
