import { createContext, useMemo, type ReactNode } from 'react';
import { useAuth } from '@/core/auth/useAuth';

interface PermissionContextValue {
    roles: string[];
    permissions: string[];
    hasRole: (role: string) => boolean;
    hasPermission: (code: string) => boolean;
    hasModuleAccess: (module: string) => boolean;
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
        return {
            roles,
            permissions,
            hasRole: (role) => roles.includes(role),
            hasPermission: (code) => permissions.includes(code),
            hasModuleAccess: (module) => permissions.some((p) => p.startsWith(`${module}.`)),
        };
    }, [user]);

    return (
        <PermissionContext.Provider value={value}>
            {children}
        </PermissionContext.Provider>
    );
};
