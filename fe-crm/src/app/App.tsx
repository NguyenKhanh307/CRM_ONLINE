import { QueryClientProvider } from '@tanstack/react-query';
import { RouterProvider } from 'react-router-dom';
import { queryClient } from '@/core/query/queryClient';
import { AuthProvider } from '@/core/auth/AuthContext';
import { PermissionProvider } from '@/core/permissions/PermissionContext';
import { AlertProvider } from '@/shared/alert/AlertContext';
import { router } from './router';

/**
 * Root component — bọc toàn bộ provider theo thứ tự:
 * QueryClientProvider → AuthProvider → PermissionProvider → RouterProvider
 */
export const App = () => {
    return (
        <QueryClientProvider client={queryClient}>
            <AuthProvider>
                <PermissionProvider>
                    <AlertProvider>
                        <RouterProvider router={router} />
                    </AlertProvider>
                </PermissionProvider>
            </AuthProvider>
        </QueryClientProvider>
    );
};
