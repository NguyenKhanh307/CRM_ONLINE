import { useContext } from 'react';
import { AlertContext } from './AlertContext';

// hook truy cập AlertContext — dùng để hiển thị popup thông báo từ bất kỳ component nào
export const useAlert = () => {
    const ctx = useContext(AlertContext);
    if (!ctx) throw new Error('useAlert phải dùng bên trong AlertProvider');
    return ctx;
};
