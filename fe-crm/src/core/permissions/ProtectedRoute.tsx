import { type ReactNode } from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '@/core/auth/useAuth';
import { usePermission } from './usePermission';

interface ProtectedRouteProps {
    children: ReactNode;
    requiredRole?: string;
}

/**
 * Bảo vệ route theo trạng thái xác thực và role.
 * - Chưa đăng nhập → chuyển về /login.
 * - Không có role yêu cầu → chuyển về /forbidden.
 */
export const ProtectedRoute = ({ children, requiredRole }: ProtectedRouteProps) => {
    const { isAuthenticated } = useAuth();
    const { hasRole } = usePermission();

    if (!isAuthenticated) return <Navigate to="/login" replace />;
    if (requiredRole && !hasRole(requiredRole)) return <Navigate to="/forbidden" replace />;

    return <>{children}</>;
};
