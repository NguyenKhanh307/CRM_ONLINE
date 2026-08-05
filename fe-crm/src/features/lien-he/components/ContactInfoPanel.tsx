import { InfoRow } from '@/shared/components/detail/InfoRow';
import { formatISODate } from '@/shared/utils/date';
import type { ContactResult } from '../types/contactTypes';

const GENDER_LABELS: Record<string, string> = { male: 'Nam', female: 'Nữ', other: 'Khác' };

interface Props {
    contact: ContactResult;
}

/** Thông tin cột trái của trang chi tiết Liên hệ. */
export const ContactInfoPanel = ({ contact: c }: Props) => (
    <div>
        <InfoRow label="Họ tên" value={c.fullName} />
        <InfoRow label="Danh xưng" value={c.salutation} />
        <InfoRow label="Chức danh" value={c.title} />
        <InfoRow label="Phòng ban" value={c.department} />
        <InfoRow label="Khách hàng" value={c.customerName} />
        <InfoRow label="Email" value={c.email} />
        <InfoRow label="Zalo" value={c.zalo} />
        <InfoRow label="Giới tính" value={c.gender ? (GENDER_LABELS[c.gender] ?? c.gender) : null} />
        <InfoRow label="Ngày sinh" value={c.dateOfBirth ? formatISODate(c.dateOfBirth) : null} />
        <InfoRow label="Nguồn" value={c.source} />
        <InfoRow label="Liên hệ chính" value={c.isPrimary ? 'Có' : 'Không'} />
        <InfoRow label="Người phụ trách" value={c.assignedUserName} />
        <InfoRow label="Người tạo" value={c.createdByName} />
        <InfoRow label="Ngày tạo" value={c.createdAt ? formatISODate(c.createdAt) : null} />
        <InfoRow label="Người sửa cuối" value={c.updatedByName} />
        <InfoRow label="Ngày sửa" value={c.updatedAt ? formatISODate(c.updatedAt) : null} />
    </div>
);
