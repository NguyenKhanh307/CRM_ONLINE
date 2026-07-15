import { InfoRow } from '@/shared/components/detail/InfoRow';
import { formatCurrency } from '@/shared/utils/number';
import { formatISODate } from '@/shared/utils/date';
import type { QuotationResult } from '../types/quotationTypes';

interface Props {
    quotation: QuotationResult;
}

/** Thông tin cột trái của trang chi tiết Báo giá. */
export const QuotationInfoPanel = ({ quotation: q }: Props) => (
    <div>
        <InfoRow label="Mã" value={q.code} />
        <InfoRow label="Khách hàng" value={q.customerName} />
        <InfoRow label="Liên hệ" value={q.contactName} />
        <InfoRow label="Cơ hội" value={q.opportunityName} />
        <InfoRow label="Người phụ trách" value={q.ownerName} />
        <InfoRow label="Ngày báo giá" value={q.quoteDate ? formatISODate(q.quoteDate) : null} />
        <InfoRow label="Hiệu lực đến" value={q.validUntil ? formatISODate(q.validUntil) : null} />
        <InfoRow label="Tiền tệ" value={q.currency} />
        <InfoRow label="Tạm tính" value={q.subtotal != null ? formatCurrency(q.subtotal) : null} />
        <InfoRow label="Chiết khấu" value={q.discount != null ? formatCurrency(q.discount) : null} />
        <InfoRow label="Thuế" value={q.tax != null ? formatCurrency(q.tax) : null} />
        <InfoRow label="Tổng tiền" value={q.total != null ? formatCurrency(q.total) : null} />
        <InfoRow label="Báo giá chính" value={q.isPrimary ? 'Có' : 'Không'} />
        <InfoRow label="Đã khóa" value={q.isLocked ? 'Có' : 'Không'} />
        <InfoRow label="Ghi chú" value={q.note} />
        <InfoRow label="Người tạo" value={q.createdByName} />
        <InfoRow label="Ngày tạo" value={q.createdAt ? formatISODate(q.createdAt) : null} />
        <InfoRow label="Người sửa cuối" value={q.updatedByName} />
        <InfoRow label="Ngày sửa" value={q.updatedAt ? formatISODate(q.updatedAt) : null} />
    </div>
);
