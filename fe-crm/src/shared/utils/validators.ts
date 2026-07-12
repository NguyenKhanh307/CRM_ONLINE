/**
 * Hàm kiểm tra dữ liệu nhập dùng chung cho form (khớp ràng buộc backend).
 * Mỗi hàm trả về thông báo lỗi (string) hoặc `null` nếu hợp lệ.
 * Giá trị rỗng được coi là hợp lệ (dùng kèm kiểm tra bắt buộc riêng).
 */

const EMAIL_RE = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const PHONE_RE = /^[0-9+.() -]{8,15}$/;
const TAX_CODE_RE = /^[0-9-]{10,14}$/;

const isEmpty = (v: string | number | null | undefined): boolean =>
    v === null || v === undefined || String(v).trim() === '';

/** Email đúng định dạng. */
export const emailError = (v: string | null | undefined): string | null =>
    isEmpty(v) || EMAIL_RE.test(String(v).trim()) ? null : 'Email không hợp lệ';

/** Số điện thoại: 8–15 ký tự số (cho phép + . ( ) - và khoảng trắng). */
export const phoneError = (v: string | null | undefined): string | null =>
    isEmpty(v) || PHONE_RE.test(String(v).trim()) ? null : 'Số điện thoại không hợp lệ';

/** Mã số thuế: 10–14 chữ số (cho phép dấu gạch ngang). */
export const taxCodeError = (v: string | null | undefined): string | null =>
    isEmpty(v) || TAX_CODE_RE.test(String(v).trim()) ? null : 'Mã số thuế không hợp lệ (10–14 chữ số)';

/** Số tiền / giá trị không được âm. */
export const nonNegativeError = (v: string | number | null | undefined, label = 'Giá trị'): string | null => {
    if (isEmpty(v)) return null;
    const n = Number(v);
    if (Number.isNaN(n)) return `${label} phải là số`;
    return n < 0 ? `${label} không được âm` : null;
};

/** Số lượng phải lớn hơn 0. */
export const positiveError = (v: string | number | null | undefined, label = 'Số lượng'): string | null => {
    if (isEmpty(v)) return null;
    const n = Number(v);
    if (Number.isNaN(n)) return `${label} phải là số`;
    return n <= 0 ? `${label} phải lớn hơn 0` : null;
};

/** Phần trăm phải trong khoảng 0–100 (thuế suất, chiết khấu %, xác suất). */
export const percentError = (v: string | number | null | undefined, label = 'Giá trị'): string | null => {
    if (isEmpty(v)) return null;
    const n = Number(v);
    if (Number.isNaN(n)) return `${label} phải là số`;
    return n < 0 || n > 100 ? `${label} phải từ 0 đến 100` : null;
};

/**
 * Ngày không được là quá khứ — CHỈ dùng ở form Thêm mới.
 * Form Sửa KHÔNG gọi hàm này (bản ghi cũ có ngày quá khứ hợp lệ, vẫn phải sửa được).
 */
export const pastDateError = (v: string | null | undefined, label = 'Ngày'): string | null => {
    if (isEmpty(v)) return null;
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const d = new Date(String(v));
    if (Number.isNaN(d.getTime())) return `${label} không hợp lệ`;
    d.setHours(0, 0, 0, 0);
    return d < today ? `${label} không được là ngày quá khứ` : null;
};

/** Ngày kết thúc không được trước ngày bắt đầu — dùng ở CẢ form Thêm và Sửa. */
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

/** Giá bán không được nhỏ hơn giá vốn. */
export const sellPriceError = (
    basePrice: string | number | null | undefined,
    costPrice: string | number | null | undefined,
): string | null => {
    if (isEmpty(basePrice) || isEmpty(costPrice)) return null;
    return Number(basePrice) < Number(costPrice) ? 'Giá bán không được nhỏ hơn giá vốn' : null;
};

/** Ngày hôm nay dạng yyyy-mm-dd — dùng cho thuộc tính `min` của ô chọn ngày ở form Thêm mới. */
export const todayISO = (): string => new Date().toISOString().slice(0, 10);

/** Gom các lỗi thành map field→message, bỏ các mục null. */
export const collectErrors = (
    entries: Record<string, string | null>,
): Record<string, string> =>
    Object.fromEntries(Object.entries(entries).filter(([, v]) => v !== null)) as Record<string, string>;
