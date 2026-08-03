// mỗi loại input có 1 file riêng trong thư mục này — mở đúng file là thấy hết mọi trường hợp
// báo lỗi của loại input đó (vd input email -> xem email.ts). File này chỉ gom lại để import
// gọn: import { emailError, requiredError } from '@/shared/utils/validators'
export * from './common';
export * from './required';
export * from './email';
export * from './phone';
export * from './taxCode';
export * from './number';
export * from './date';
export * from './price';
