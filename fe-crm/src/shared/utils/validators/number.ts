import { isEmpty } from './common';

// TẤT CẢ trường hợp báo lỗi của các ô nhập số (tiền, số lượng, %) trong app đều nằm ở đây.

// số tiền / giá trị không được âm — dùng cho estimatedValue, basePrice, costPrice...
export const nonNegativeError = (v: string | number | null | undefined, label = 'Giá trị'): string | null => {
    if (isEmpty(v)) return null;
    const n = Number(v);
    if (Number.isNaN(n)) return `${label} phải là số`;
    return n < 0 ? `${label} không được âm` : null;
};

// số lượng phải lớn hơn 0 — dùng cho quantity dòng hàng
export const positiveError = (v: string | number | null | undefined, label = 'Số lượng'): string | null => {
    if (isEmpty(v)) return null;
    const n = Number(v);
    if (Number.isNaN(n)) return `${label} phải là số`;
    return n <= 0 ? `${label} phải lớn hơn 0` : null;
};

// phần trăm phải trong khoảng 0-100 — dùng cho thuế suất, chiết khấu %, xác suất
export const percentError = (v: string | number | null | undefined, label = 'Giá trị'): string | null => {
    if (isEmpty(v)) return null;
    const n = Number(v);
    if (Number.isNaN(n)) return `${label} phải là số`;
    return n < 0 || n > 100 ? `${label} phải từ 0 đến 100` : null;
};
