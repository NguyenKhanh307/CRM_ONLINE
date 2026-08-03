import { InfoRow } from '@/shared/components/detail/InfoRow';
import { formatCurrency } from '@/shared/utils/number';
import { formatISODate } from '@/shared/utils/date';
import type { InvoiceResult } from '../types/invoiceTypes';

const PAYMENT_LABELS: Record<string, string> = {
    unpaid: 'Chưa thanh toán', partial: 'Thanh toán một phần', paid: 'Đã thanh toán', overdue: 'Quá hạn',
};

interface Props {
    invoice: InvoiceResult;
}

// thông tin cột trái của trang chi tiết Hóa đơn
export const InvoiceInfoPanel = ({ invoice: i }: Props) => (
    <div>
        <InfoRow label="Mã" value={i.code} />
        <InfoRow label="Khách hàng" value={i.customerName} />
        <InfoRow label="Liên hệ" value={i.contactName} />
        <InfoRow label="Đơn hàng" value={i.orderCode} />
        <InfoRow label="Báo giá nguồn" value={i.quotationCode} />
        <InfoRow label="Cơ hội" value={i.opportunityName} />
        <InfoRow label="Chiến dịch" value={i.campaignName} />
        <InfoRow label="Người phụ trách" value={i.ownerName} />
        <InfoRow label="Ngày hóa đơn" value={i.invoiceDate ? formatISODate(i.invoiceDate) : null} />
        <InfoRow label="Hạn thanh toán" value={i.dueDate ? formatISODate(i.dueDate) : null} />
        <InfoRow label="Trạng thái thanh toán" value={PAYMENT_LABELS[i.paymentStatus] ?? i.paymentStatus} />
        <InfoRow label="Tiền tệ" value={i.currency} />
        <InfoRow label="Tạm tính" value={i.subtotal != null ? formatCurrency(i.subtotal) : null} />
        <InfoRow label="Chiết khấu" value={i.discount != null ? formatCurrency(i.discount) : null} />
        <InfoRow label="Thuế" value={i.tax != null ? formatCurrency(i.tax) : null} />
        <InfoRow label="Tổng tiền" value={i.total != null ? formatCurrency(i.total) : null} />
        <InfoRow label="Địa chỉ xuất HĐ" value={i.billingAddress} />
        <InfoRow label="Mã số thuế" value={i.taxCode} />
        <InfoRow label="Đã khóa" value={i.isLocked ? 'Có' : 'Không'} />
        <InfoRow label="Ghi chú" value={i.note} />
        <InfoRow label="Người tạo" value={i.createdByName} />
        <InfoRow label="Ngày tạo" value={i.createdAt ? formatISODate(i.createdAt) : null} />
        <InfoRow label="Người sửa cuối" value={i.updatedByName} />
        <InfoRow label="Ngày sửa" value={i.updatedAt ? formatISODate(i.updatedAt) : null} />
    </div>
);
