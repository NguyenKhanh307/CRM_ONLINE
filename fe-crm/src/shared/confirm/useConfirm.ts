import { useContext } from 'react';
import { ConfirmContext } from './ConfirmContext';

/**
 * Hook truy cập ConfirmContext — mở popup xác nhận và chờ câu trả lời.
 *
 * Dùng `confirmCreate` / `confirmSave` cho hai luồng phổ biến; `confirm` thô dành cho
 * trường hợp cần `confirmDanger` hoặc nhãn nút riêng (ví dụ popup xóa).
 *
 * @example
 * const { confirmSave } = useConfirm();
 * if (!(await confirmSave('tiềm năng'))) return;
 */
export const useConfirm = () => {
    const ctx = useContext(ConfirmContext);
    if (!ctx) throw new Error('useConfirm phải dùng bên trong ConfirmProvider');
    return ctx;
};
