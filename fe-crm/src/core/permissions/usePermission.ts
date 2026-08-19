import { useContext } from 'react';
import { PermissionContext } from './PermissionContext';

// hook kiểm tra quyền của user hiện tại — phải dùng bên trong PermissionProvider
export const usePermission = () => {
    const ctx = useContext(PermissionContext);
    // nếu không có provider thì throw lỗi, vì phải đặt bên trong PermissionProvider
    if (!ctx) throw new Error('Phải sử dụng usePermission bên trong PermissionProvider');
    return ctx;
};
