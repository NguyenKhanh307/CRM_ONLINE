import { isEmpty } from './common';

// TẤT CẢ trường hợp báo lỗi "bắt buộc nhập" của mọi form trong app đều đi qua đây —
// đổi câu chữ ở 1 chỗ này là đổi cho toàn bộ ứng dụng
export const requiredError = (v: string | number | null | undefined, label: string): string | null =>
    isEmpty(v) ? `${label} không được để trống` : null;
