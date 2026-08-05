import { InfoRow } from '@/shared/components/detail/InfoRow';
import { formatCurrency } from '@/shared/utils/number';
import { formatISODate } from '@/shared/utils/date';
import { useOrderDetail } from '@/features/don-hang/hooks/useOrderDetail';
import { useQuotationDetail } from '@/features/bao-gia/hooks/useQuotationDetail';
import type { InvoiceResult } from '../types/invoiceTypes';

const PAYMENT_LABELS: Record<string, string> = {
    unpaid: 'Chưa thanh toán', partial: 'Thanh toán một phần', paid: 'Đã thanh toán', overdue: 'Quá hạn',
};

interface Props {
    invoice: InvoiceResult;
}

// thông tin cột trái của trang chi tiết Hóa đơn. Không còn customerId/contactId/quotationId/
// opportunityId/campaignId trực tiếp — tra qua chuỗi orderId -> quotationId
export const InvoiceInfoPanel = ({ invoice: i }: Props) => {
    const { data: order } = useOrderDetail(i.orderId ?? undefined);
    const { data: quotation } = useQuotationDetail(order?.quotationId ?? undefined);
    return (
        <div>
            <InfoRow label="Mã" value={i.code} />
            <InfoRow label="Khách hàng" value={quotation?.customerName} />
            <InfoRow label="Liên hệ" value={quotation?.contactName} />
            <InfoRow label="Đơn hàng" value={i.orderCode} />
            <InfoRow label="Báo giá nguồn" value={order?.quotationCode} />
            <InfoRow label="Cơ hội" value={quotation?.opportunityName} />
            <InfoRow label="Người phụ trách" value={i.ownerName} />
            <InfoRow label="Ngày hóa đơn" value={i.invoiceDate ? formatISODate(i.invoiceDate) : null} />
            <InfoRow label="Hạn thanh toán" value={i.dueDate ? formatISODate(i.dueDate) : null} />
            <InfoRow label="Trạng thái thanh toán" value={PAYMENT_LABELS[i.paymentStatus] ?? i.paymentStatus} />
            <InfoRow label="Tạm tính" value={i.subtotal != null ? formatCurrency(i.subtotal) : null} />
            <InfoRow label="Chiết khấu" value={i.discount != null ? formatCurrency(i.discount) : null} />
            <InfoRow label="Thuế" value={i.tax != null ? formatCurrency(i.tax) : null} />
            <InfoRow label="Tổng tiền" value={i.total != null ? formatCurrency(i.total) : null} />
            <InfoRow label="Đã khóa" value={i.isLocked ? 'Có' : 'Không'} />
            <InfoRow label="Ghi chú" value={i.note} />
            <InfoRow label="Người tạo" value={i.createdByName} />
            <InfoRow label="Ngày tạo" value={i.createdAt ? formatISODate(i.createdAt) : null} />
            <InfoRow label="Người sửa cuối" value={i.updatedByName} />
            <InfoRow label="Ngày sửa" value={i.updatedAt ? formatISODate(i.updatedAt) : null} />
        </div>
    );
};
