import { createContext, useMemo, type ReactNode } from 'react';
import { useAuth } from '@/core/auth/useAuth';

// định nghĩa các action có thể kiểm tra quyền
// Dùng xét quyền thao tác trên module, ánh xạ từ @PreAuthorize("hasAuthority('module.action')") của backend
export type PermAction =
    | 'view' | 'create' | 'edit' | 'delete'
    | 'import' | 'export' | 'handover'
    | 'approve' | 'process' | 'approve_return'
    | 'submit' | 'send' | 'activate' | 'change_stage'
    | 'create_invoice' | 'send_email' | 'convert';
// định nghĩa interface cho context quyền
interface PermissionContextValue {
    roles: string[];
    permissions: string[];
    hasRole: (role: string) => boolean;
    hasPermission: (code: string) => boolean;
    hasModuleAccess: (module: string) => boolean;
    // kiểm tra quyền thao tác trên module, ánh xạ từ @PreAuthorize("hasAuthority('module.action')") của backend
    can: (module: string, action: PermAction) => boolean;
}
// tạo context để cung cấp danh sách role và permission của user hiện tại
export const PermissionContext = createContext<PermissionContextValue | null>(null);

interface PermissionProviderProps {
    children: ReactNode;
}

// provider cung cấp danh sách role của user hiện tại
// phụ thuộc vào AuthProvider — phải đặt bên trong AuthProvider
export const PermissionProvider = ({ children }: PermissionProviderProps) => {
    const { user } = useAuth();
    // tính toán danh sách role và permission từ user hiện tại, memo để tránh re-render không cần thiết
    const value = useMemo<PermissionContextValue>(() => {
        const roles = user?.roles ?? [];
        const permissions = user?.permissions ?? [];
        // hàm kiểm tra quyền thao tác trên module, ánh xạ từ @PreAuthorize("hasAuthority('module.action')") của backend
        const has = (code: string) => permissions.includes(code);
        // kiểm tra xem user có phải là manager (ADMIN hoặc SALES_MANAGER) để cho phép handover
        const isManager = roles.includes('ADMIN') || roles.includes('SALES_MANAGER');
        return {
            roles,
            permissions,
            hasRole: (role) => roles.includes(role),
            hasPermission: has,
            // kiểm tra xem user có quyền truy cập module (có permission bắt đầu bằng 'module.')
            hasModuleAccess: (module) => permissions.some((p) => p.startsWith(`${module}.`)),
            can: (module, action) => {
                switch (action) {
                    case 'handover':
                        return isManager;
                    default:
                        return has(`${module}.${action}`);
                }
            },
        };
    }, [user]);

    return (
        // cung cấp context quyền cho toàn app
        <PermissionContext.Provider value={value}>
            {children}
        </PermissionContext.Provider>
    );
};
