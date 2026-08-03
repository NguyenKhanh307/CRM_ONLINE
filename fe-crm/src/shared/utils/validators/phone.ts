import { isEmpty } from './common';

const PHONE_RE = /^[0-9+.() -]{8,15}$/;

// TẤT CẢ trường hợp báo lỗi của ô nhập số điện thoại trong app đều nằm ở đây.
// quy tắc: 8-15 ký tự số, cho phép + . ( ) - và khoảng trắng.
export const phoneError = (v: string | null | undefined): string | null =>
    isEmpty(v) || PHONE_RE.test(String(v).trim()) ? null : 'Số điện thoại không hợp lệ';
