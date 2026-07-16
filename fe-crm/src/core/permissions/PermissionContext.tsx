import { createContext, useMemo, type ReactNode } from 'react';
import { useAuth } from '@/core/auth/useAuth';

/** Các thao tác có thể gate quyền trên UI. Khớp guard BE (@PreAuthorize). */
export type PermAction =
    | 'view' | 'create' | 'edit' | 'delete'
    | 'import' | 'export' | 'handover'
    | 'approve' | 'process' | 'approve_return';

/**
 * Module chỉ có quyền `.view` (không có create/edit/delete lẻ) → quản lý bằng role
 * ADMIN/SALES_MANAGER (khớp BE gate product mutating theo role).
 */
const MANAGE_BY_ROLE = new Set(['product']);
/**
 * Module không có code `.delete` riêng → BE gate xóa bằng role ADMIN/SALES_MANAGER
 * (quotation/invoice/product). Với các module này, `edit` fallback về `.create`.
 */
const DELETE_BY_ROLE = new Set(['quotation', 'invoice', 'product']);

interface PermissionContextValue {
    roles: string[];
    permissions: string[];
    hasRole: (role: string) => boolean;
    hasPermission: (code: string) => boolean;
    hasModuleAccess: (module: string) => boolean;
    /**
     * Nguồn sự thật duy nhất cho việc ẩn/hiện nút thao tác — ánh xạ khớp guard BE.
     * @param module key module (vd 'lead', 'quotation')
     * @param action thao tác cần kiểm quyền
     */
    can: (module: string, action: PermAction) => boolean;
}

export const PermissionContext = createContext<PermissionContextValue | null>(null);

interface PermissionProviderProps {
    children: ReactNode;
}

/**
 * Provider cung cấp danh sách role của user hiện tại.
 * Phụ thuộc vào AuthProvider — phải đặt bên trong AuthProvider.
 */
export const PermissionProvider = ({ children }: PermissionProviderProps) => {
    const { user } = useAuth();

    const value = useMemo<PermissionContextValue>(() => {
        const roles = user?.roles ?? [];
        const permissions = user?.permissions ?? [];
        const has = (code: string) => permissions.includes(code);
        const isManager = roles.includes('ADMIN') || roles.includes('SALES_MANAGER');
        return {
            roles,
            permissions,
            hasRole: (role) => roles.includes(role),
            hasPermission: has,
            hasModuleAccess: (module) => permissions.some((p) => p.startsWith(`${module}.`)),
            can: (module, action) => {
                switch (action) {
                    case 'create':
                    case 'import':
                        return MANAGE_BY_ROLE.has(module) ? isManager : has(`${module}.create`);
                    case 'edit':
                        if (MANAGE_BY_ROLE.has(module)) return isManager;
                        return has(`${module}.edit`) || (DELETE_BY_ROLE.has(module) && has(`${module}.create`));
                    case 'delete':
                        return DELETE_BY_ROLE.has(module) ? isManager : has(`${module}.delete`);
                    case 'export':
                        return has(`${module}.export`);
                    case 'handover':
                        return isManager;
                    default:
                        // view, approve, process, approve_return
                        return has(`${module}.${action}`);
                }
            },
        };
    }, [user]);

    return (
        <PermissionContext.Provider value={value}>
            {children}
        </PermissionContext.Provider>
    );
};
