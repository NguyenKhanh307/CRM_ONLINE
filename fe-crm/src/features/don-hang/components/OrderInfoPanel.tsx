import { InfoRow } from '@/shared/components/detail/InfoRow';
import { formatCurrency } from '@/shared/utils/number';
import { formatISODate } from '@/shared/utils/date';
import { useQuotationDetail } from '@/features/bao-gia/hooks/useQuotationDetail';
import type { OrderResult } from '../types/orderTypes';

interface Props {
    order: OrderResult;
}

// thông tin cột trái của trang chi tiết Đơn hàng. Không còn customerId/contactId/opportunityId/
// campaignId trực tiếp — khách hàng/liên hệ/cơ hội tra qua báo giá nguồn (quotationId)
export const OrderInfoPanel = ({ order: o }: Props) => {
    const { data: quotation } = useQuotationDetail(o.quotationId ?? undefined);
    return (
        <div>
            <InfoRow label="Mã" value={o.code} />
            <InfoRow label="Khách hàng" value={quotation?.customerName} />
            <InfoRow label="Liên hệ" value={quotation?.contactName} />
            <InfoRow label="Báo giá nguồn" value={o.quotationCode} />
            <InfoRow label="Cơ hội" value={quotation?.opportunityName} />
            <InfoRow label="Người phụ trách" value={o.ownerName} />
            <InfoRow label="Ngày đặt" value={o.orderDate ? formatISODate(o.orderDate) : null} />
            <InfoRow label="Ngày giao" value={o.deliveryDate ? formatISODate(o.deliveryDate) : null} />
            <InfoRow label="Tạm tính" value={o.subtotal != null ? formatCurrency(o.subtotal) : null} />
            <InfoRow label="Chiết khấu" value={o.discount != null ? formatCurrency(o.discount) : null} />
            <InfoRow label="Thuế" value={o.tax != null ? formatCurrency(o.tax) : null} />
            <InfoRow label="Tổng tiền" value={o.total != null ? formatCurrency(o.total) : null} />
            <InfoRow label="Đã khóa" value={o.isLocked ? 'Có' : 'Không'} />
            <InfoRow label="Ghi chú" value={o.note} />
            <InfoRow label="Người tạo" value={o.createdByName} />
            <InfoRow label="Ngày tạo" value={o.createdAt ? formatISODate(o.createdAt) : null} />
            <InfoRow label="Người sửa cuối" value={o.updatedByName} />
            <InfoRow label="Ngày sửa" value={o.updatedAt ? formatISODate(o.updatedAt) : null} />
        </div>
    );
};
