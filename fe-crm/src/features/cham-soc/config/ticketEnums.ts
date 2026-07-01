/** Nhãn tiếng Việt + màu badge cho các enum của phiếu hỗ trợ — dùng chung bảng/chi tiết/form. */

export const TYPE_LABELS: Record<string, string> = {
    support: 'Hỗ trợ', return: 'Trả hàng', exchange: 'Đổi hàng', complaint: 'Khiếu nại',
};
export const TYPE_COLORS: Record<string, string> = {
    support: 'bg-blue-100 text-blue-700', return: 'bg-orange-100 text-orange-700',
    exchange: 'bg-purple-100 text-purple-700', complaint: 'bg-red-100 text-red-600',
};

export const STATUS_LABELS: Record<string, string> = {
    new: 'Mới', assigned: 'Đã giao', in_progress: 'Đang xử lý', approved: 'Đã duyệt',
    rejected: 'Từ chối', received: 'Đã nhận hàng', inspected: 'Đã kiểm hàng',
    resolved: 'Đã giải quyết', closed: 'Đã đóng', reopened: 'Mở lại',
};
export const STATUS_COLORS: Record<string, string> = {
    new: 'bg-gray-100 text-gray-600', assigned: 'bg-cyan-100 text-cyan-700',
    in_progress: 'bg-blue-100 text-blue-700', approved: 'bg-green-100 text-green-700',
    rejected: 'bg-red-100 text-red-600', received: 'bg-indigo-100 text-indigo-700',
    inspected: 'bg-violet-100 text-violet-700', resolved: 'bg-teal-100 text-teal-700',
    closed: 'bg-gray-200 text-gray-700', reopened: 'bg-amber-100 text-amber-700',
};

export const PRIORITY_LABELS: Record<string, string> = {
    low: 'Thấp', medium: 'Thường', high: 'Cao', urgent: 'Khẩn',
};
export const PRIORITY_COLORS: Record<string, string> = {
    low: 'bg-gray-100 text-gray-600', medium: 'bg-blue-100 text-blue-700',
    high: 'bg-orange-100 text-orange-700', urgent: 'bg-red-100 text-red-600',
};

export const CHANNEL_LABELS: Record<string, string> = {
    phone: 'Điện thoại', email: 'Email', web: 'Web', zalo: 'Zalo', other: 'Khác',
};

export const REASON_LABELS: Record<string, string> = {
    defective: 'Lỗi kỹ thuật', wrong_item: 'Giao sai hàng', not_as_described: 'Không như mô tả',
    changed_mind: 'Đổi ý', late_delivery: 'Giao trễ', other: 'Khác',
};

export const RESOLUTION_LABELS: Record<string, string> = {
    refund: 'Hoàn tiền', replacement: 'Đổi hàng', repair: 'Sửa chữa',
    store_credit: 'Ghi có', answered: 'Đã giải đáp', rejected: 'Từ chối',
};

/** Danh sách option cho dropdown form (value + label). */
export const TYPE_OPTIONS = Object.entries(TYPE_LABELS).map(([value, label]) => ({ value, label }));
export const CHANNEL_OPTIONS = Object.entries(CHANNEL_LABELS).map(([value, label]) => ({ value, label }));
export const PRIORITY_OPTIONS = Object.entries(PRIORITY_LABELS).map(([value, label]) => ({ value, label }));
export const REASON_OPTIONS = Object.entries(REASON_LABELS).map(([value, label]) => ({ value, label }));
export const RESOLUTION_OPTIONS = Object.entries(RESOLUTION_LABELS).map(([value, label]) => ({ value, label }));
