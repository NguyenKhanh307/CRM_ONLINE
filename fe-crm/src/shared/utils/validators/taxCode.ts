import { isEmpty } from './common';

const TAX_CODE_RE = /^[0-9-]{10,14}$/;

// TẤT CẢ trường hợp báo lỗi của ô nhập mã số thuế trong app đều nằm ở đây.
// quy tắc: 10-14 chữ số, cho phép dấu gạch ngang.
export const taxCodeError = (v: string | null | undefined): string | null =>
    isEmpty(v) || TAX_CODE_RE.test(String(v).trim()) ? null : 'Mã số thuế không hợp lệ (10–14 chữ số)';
