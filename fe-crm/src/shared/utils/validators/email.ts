import { isEmpty } from './common';

const EMAIL_RE = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

// TẤT CẢ trường hợp báo lỗi của ô nhập email trong app đều nằm ở đây.
// hiện chỉ có 1 trường hợp: sai định dạng. Trường hợp "bắt buộc nhập" dùng requiredError riêng.
export const emailError = (v: string | null | undefined): string | null =>
    isEmpty(v) || EMAIL_RE.test(String(v).trim()) ? null : 'Email không hợp lệ';
