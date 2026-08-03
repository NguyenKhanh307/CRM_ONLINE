import { isEmpty } from './common';

// TẤT CẢ trường hợp báo lỗi của các ô nhập ngày trong app đều nằm ở đây.

// ngày không được là quá khứ — CHỈ dùng ở form Thêm mới (form Sửa không gọi hàm này,
// bản ghi cũ có ngày quá khứ hợp lệ, vẫn phải sửa được)
export const pastDateError = (v: string | null | undefined, label = 'Ngày'): string | null => {
    if (isEmpty(v)) return null;
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const d = new Date(String(v));
    if (Number.isNaN(d.getTime())) return `${label} không hợp lệ`;
    d.setHours(0, 0, 0, 0);
    return d < today ? `${label} không được là ngày quá khứ` : null;
};

// ngày kết thúc không được trước ngày bắt đầu — dùng ở CẢ form Thêm và Sửa
export const dateRangeError = (
    start: string | null | undefined,
    end: string | null | undefined,
    startLabel = 'ngày bắt đầu',
    endLabel = 'Ngày kết thúc',
): string | null => {
    if (isEmpty(start) || isEmpty(end)) return null;
    return new Date(String(end)) < new Date(String(start))
        ? `${endLabel} không được trước ${startLabel}`
        : null;
};

// ngày hôm nay dạng yyyy-mm-dd — dùng cho thuộc tính min của ô chọn ngày ở form Thêm mới
export const todayISO = (): string => new Date().toISOString().slice(0, 10);
