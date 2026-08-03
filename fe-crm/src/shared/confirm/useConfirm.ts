import { useContext } from 'react';
import { ConfirmContext } from './ConfirmContext';

// hook truy cập ConfirmContext — mở popup xác nhận và chờ câu trả lời
// dùng confirmCreate / confirmSave cho hai luồng phổ biến; confirm thô dành cho
// trường hợp cần confirmDanger hoặc nhãn nút riêng (ví dụ popup xóa)
export const useConfirm = () => {
    const ctx = useContext(ConfirmContext);
    if (!ctx) throw new Error('useConfirm phải dùng bên trong ConfirmProvider');
    return ctx;
};
