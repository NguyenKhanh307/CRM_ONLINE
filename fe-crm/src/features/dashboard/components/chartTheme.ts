// bảng màu + nhãn tiếng Việt dùng chung cho biểu đồ Dashboard

// token màu chính (khớp tailwind.config.js)
export const COLORS = {
    primary: '#1677ff',
    success: '#52c41a',
    warning: '#faad14',
    danger: '#ff4d4f',
} as const;

// palette phân loại cho chart nhiều thành phần (donut, stacked bar)
export const PALETTE = [
    '#1677ff', '#52c41a', '#faad14', '#ff4d4f', '#722ed1',
    '#13c2c2', '#eb2f96', '#fa8c16', '#2f54eb', '#8c8c8c',
];

// lấy màu theo chỉ số (vòng lại khi vượt độ dài palette)
export const colorAt = (i: number): string => PALETTE[i % PALETTE.length];

// nhãn trạng thái tiếng Việt theo từng phân hệ
export const STATUS_LABELS: Record<string, Record<string, string>> = {
    user: { active: 'Hoạt động', inactive: 'Ngưng', locked: 'Khóa' },
    lead: { new: 'Mới', contacting: 'Đang liên hệ', converted: 'Đã chuyển đổi' },
    opportunity: { open: 'Đang mở', won: 'Thắng', lost: 'Thua' },
    order: { draft: 'Nháp', confirmed: 'Đã xác nhận', processing: 'Đang xử lý', completed: 'Hoàn thành', cancelled: 'Đã hủy' },
    invoice: { draft: 'Nháp', sent: 'Đã gửi', partially_paid: 'TT một phần', paid: 'Đã thanh toán', cancelled: 'Đã hủy' },
    ticket: {
        new: 'Mới', assigned: 'Đã phân công', in_progress: 'Đang xử lý', approved: 'Đã duyệt', rejected: 'Từ chối',
        received: 'Đã nhận', inspected: 'Đã kiểm', resolved: 'Đã giải quyết', closed: 'Đóng', reopened: 'Mở lại',
    },
};

// dịch nhãn trạng thái thô sang tiếng Việt theo phân hệ; giữ nguyên nếu không có ánh xạ
export const viStatus = (module: string, raw: string): string =>
    STATUS_LABELS[module]?.[raw] ?? raw;

// rút gọn nhãn kỳ "2026-07" -> "Th7" cho trục hoành biểu đồ theo tháng
export const shortMonth = (period: string): string => {
    const m = period.split('-')[1];
    return m ? `Th${parseInt(m, 10)}` : period;
};
