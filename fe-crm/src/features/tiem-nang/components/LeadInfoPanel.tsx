import { InfoRow } from '@/shared/components/detail/InfoRow';
import { formatNumber } from '@/shared/utils/number';
import { formatISODate } from '@/shared/utils/date';
import type { LeadResult } from '../types/leadTypes';

const TYPE_LABELS: Record<string, string> = { individual: 'Cá nhân', company: 'Công ty' };

interface Props {
    lead: LeadResult;
}

// thông tin cột trái của trang chi tiết Tiềm năng
export const LeadInfoPanel = ({ lead: l }: Props) => (
    <div>
        <InfoRow label="Mã" value={l.code} />
        <InfoRow label="Tên" value={l.name} />
        <InfoRow label="Công ty" value={l.companyName} />
        <InfoRow label="Loại" value={l.leadType ? (TYPE_LABELS[l.leadType] ?? l.leadType) : null} />
        <InfoRow label="Điện thoại" value={l.phone} />
        <InfoRow label="Email" value={l.email} />
        <InfoRow label="Mã số thuế" value={l.taxCode} />
        <InfoRow label="Website" value={l.website} />
        <InfoRow label="Ngành nghề" value={l.industry} />
        <InfoRow label="Nguồn" value={l.source} />
        <InfoRow label="Chiến dịch nguồn" value={l.campaignName} />
        <InfoRow label="Điểm" value={formatNumber(l.score)} />
        <InfoRow label="Người phụ trách" value={l.ownerName} />
        <InfoRow label="Liên hệ liên kết" value={l.contactName} />
        <InfoRow label="Cơ hội đã chuyển đổi" value={l.convertedOpportunityId != null ? `#${l.convertedOpportunityId}` : null} />
        <InfoRow label="Ghi chú" value={l.note} />
        <InfoRow label="Người tạo" value={l.createdByName} />
        <InfoRow label="Ngày tạo" value={l.createdAt ? formatISODate(l.createdAt) : null} />
        <InfoRow label="Người sửa cuối" value={l.updatedByName} />
        <InfoRow label="Ngày sửa" value={l.updatedAt ? formatISODate(l.updatedAt) : null} />
    </div>
);
