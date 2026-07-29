import { createContext, useMemo, type ReactNode } from 'react';
import { useAuth } from '@/core/auth/useAuth';

/** Các thao tác có thể gate quyền trên UI. Khớp guard BE (@PreAuthorize). */
export type PermAction =
    | 'view' | 'create' | 'edit' | 'delete'
    | 'import' | 'export' | 'handover'
    | 'approve' | 'process' | 'approve_return'
    | 'submit' | 'send' | 'activate' | 'change_stage'
    | 'create_invoice' | 'send_email' | 'convert';

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
                    case 'handover':
                        return isManager;
                    default:
                        // view, create, edit, delete, import, export, approve, process,
                        // approve_return, submit, send, activate, change_stage, create_invoice, send_email, convert
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
