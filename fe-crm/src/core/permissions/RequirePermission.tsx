import type { ReactNode } from 'react';
import { Navigate } from 'react-router-dom';
import { usePermission } from './usePermission';
import type { PermAction } from './PermissionContext';

interface Props {
    // key module cần kiểm quyền (vd 'lead', 'quotation')
    module: string;
    // thao tác cần quyền để vào route (mặc định 'create' cho form thêm mới / nhập file)
    action?: PermAction;
    children: ReactNode;
}

// component bảo vệ route, chỉ cho phép truy cập nếu user có quyền thao tác trên module
// /{module}/them-moi và /{module}/nhap-file để chặn gõ URL trực tiếp
export function RequirePermission({ module, action = 'create', children }: Props) {
    const { can } = usePermission();
    // nếu user không có quyền thao tác trên module thì redirect về trang forbidden
    if (!can(module, action)) {
        return <Navigate to="/forbidden" replace />;
    }
    return <>{children}</>;
}
