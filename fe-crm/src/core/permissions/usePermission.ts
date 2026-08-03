import { useContext } from 'react';
import { PermissionContext } from './PermissionContext';

// hook kiểm tra quyền của user hiện tại — phải dùng bên trong PermissionProvider
export const usePermission = () => {
    const ctx = useContext(PermissionContext);
    if (!ctx) throw new Error('usePermission must be used within PermissionProvider');
    return ctx;
};
