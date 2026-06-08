import { useContext } from 'react';
import { AlertContext } from './AlertContext';

/**
 * Hook truy cập AlertContext — dùng để hiển thị popup thông báo từ bất kỳ component nào.
 *
 * @example
 * const { showAlert } = useAlert();
 * showAlert('Thông tin hàng hóa không được để trống');
 */
export const useAlert = () => {
    const ctx = useContext(AlertContext);
    if (!ctx) throw new Error('useAlert phải dùng bên trong AlertProvider');
    return ctx;
};
