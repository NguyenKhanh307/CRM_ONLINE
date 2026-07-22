import type { RelatedRecord } from '../../types/related';
import type { RelatedColumn } from './RelatedTable';

/**
 * Cấu hình cột cho từng tab bản ghi liên quan của trang chi tiết 360°.
 * Dùng chung cho cả trang Khách hàng và Cơ hội (báo giá/đơn/hóa đơn giống hệt nhau).
 */

const STATUS_LABELS: Record<string, Record<string, string>> = {
    lead: {
        new: 'Mới', contacting: 'Đang liên hệ', qualified: 'Đủ điều kiện',
        converted: 'Đã chuyển đổi', lost: 'Thất bại',
    },
    opportunity: { open: 'Đang mở', won: 'Đã thắng', lost: 'Đã thua' },
    quotation: {
        draft: 'Nháp', pending: 'Chờ duyệt', approved: 'Đã duyệt',
        rejected: 'Từ chối', sent: 'Đã gửi', accepted: 'Khách chấp nhận', expired: 'Hết hạn',
    },
    order: {
        draft: 'Nháp', confirmed: 'Đã xác nhận', processing: 'Đang xử lý',
        completed: 'Hoàn tất', cancelled: 'Đã hủy',
    },
    invoice: {
        draft: 'Nháp', sent: 'Đã gửi', partially_paid: 'Thanh toán một phần',
        paid: 'Đã thanh toán', cancelled: 'Đã hủy',
    },
    ticket: {
        new: 'Mới', assigned: 'Đã giao', in_progress: 'Đang xử lý', approved: 'Đã duyệt',
        rejected: 'Từ chối', received: 'Đã nhận hàng', inspected: 'Đã kiểm hàng',
        resolved: 'Đã giải quyết', closed: 'Đã đóng', reopened: 'Mở lại',
    },
};

const STATUS_COLORS: Record<string, string> = {
    draft: 'bg-gray-100 text-gray-600',
    new: 'bg-gray-100 text-gray-600',
    pending: 'bg-yellow-100 text-yellow-700',
    processing: 'bg-yellow-100 text-yellow-700',
    partially_paid: 'bg-yellow-100 text-yellow-700',
    approved: 'bg-green-100 text-green-700',
    accepted: 'bg-green-100 text-green-700',
    completed: 'bg-green-100 text-green-700',
    paid: 'bg-green-100 text-green-700',
    won: 'bg-green-100 text-green-700',
    resolved: 'bg-teal-100 text-teal-700',
    rejected: 'bg-red-100 text-red-600',
    cancelled: 'bg-red-100 text-red-600',
    lost: 'bg-red-100 text-red-600',
    sent: 'bg-blue-100 text-blue-700',
    open: 'bg-blue-100 text-blue-700',
    in_progress: 'bg-blue-100 text-blue-700',
    assigned: 'bg-cyan-100 text-cyan-700',
    received: 'bg-indigo-100 text-indigo-700',
    inspected: 'bg-violet-100 text-violet-700',
    closed: 'bg-gray-200 text-gray-700',
    reopened: 'bg-amber-100 text-amber-700',
    expired: 'bg-orange-100 text-orange-700',
    contacting: 'bg-blue-100 text-blue-700',
    qualified: 'bg-teal-100 text-teal-700',
    converted: 'bg-green-100 text-green-700',
};

/** Cột trạng thái dạng badge, nhãn tra theo phân hệ của dòng. */
const statusColumn = (label = 'Trạng thái'): RelatedColumn => ({
    key: 'status',
    label,
    render: (row: RelatedRecord) => {
        if (!row.status) return '—';
        const text = STATUS_LABELS[row.module]?.[row.status] ?? row.status;
        return (
            <span className={`inline-block px-2 py-0.5 rounded text-sm font-medium ${STATUS_COLORS[row.status] ?? 'bg-gray-100 text-gray-600'}`}>
                {text}
            </span>
        );
    },
});

export const LEAD_COLUMNS: RelatedColumn[] = [
    { key: 'code', label: 'Mã tiềm năng' },
    { key: 'name', label: 'Tên tiềm năng' },
    statusColumn(),
    { key: 'date', label: 'Ngày tạo' },
    { key: 'amount', label: 'Giá trị ước tính', align: 'right' },
    { key: 'ownerName', label: 'Người phụ trách' },
];

export const CONTACT_COLUMNS: RelatedColumn[] = [
    { key: 'name', label: 'Họ tên' },
    { key: 'code', label: 'Email' },
    { key: 'status', label: 'Chức danh' },
    { key: 'ownerName', label: 'Người phụ trách' },
];

export const OPPORTUNITY_COLUMNS: RelatedColumn[] = [
    { key: 'code', label: 'Mã cơ hội' },
    { key: 'name', label: 'Tên cơ hội' },
    statusColumn(),
    { key: 'date', label: 'Ngày dự kiến chốt' },
    { key: 'amount', label: 'Giá trị', align: 'right' },
    { key: 'ownerName', label: 'Người phụ trách' },
];

export const QUOTATION_COLUMNS: RelatedColumn[] = [
    { key: 'code', label: 'Mã báo giá' },
    statusColumn(),
    { key: 'date', label: 'Ngày báo giá' },
    { key: 'amount', label: 'Tổng tiền', align: 'right' },
    { key: 'ownerName', label: 'Người phụ trách' },
];

export const ORDER_COLUMNS: RelatedColumn[] = [
    { key: 'code', label: 'Mã đơn hàng' },
    statusColumn(),
    { key: 'date', label: 'Ngày đặt' },
    { key: 'amount', label: 'Tổng tiền', align: 'right' },
    { key: 'ownerName', label: 'Người phụ trách' },
];

export const INVOICE_COLUMNS: RelatedColumn[] = [
    { key: 'code', label: 'Mã hóa đơn' },
    statusColumn(),
    { key: 'date', label: 'Ngày hóa đơn' },
    { key: 'amount', label: 'Tổng tiền', align: 'right' },
    { key: 'ownerName', label: 'Người phụ trách' },
];

export const TICKET_COLUMNS: RelatedColumn[] = [
    { key: 'code', label: 'Mã phiếu' },
    { key: 'name', label: 'Tiêu đề' },
    statusColumn(),
    { key: 'date', label: 'Ngày tạo' },
    { key: 'ownerName', label: 'Người xử lý' },
];
