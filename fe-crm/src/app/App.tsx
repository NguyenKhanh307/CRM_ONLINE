import { QueryClientProvider } from '@tanstack/react-query';
import { RouterProvider } from 'react-router-dom';
import { queryClient } from '@/core/query/queryClient';
import { AuthProvider } from '@/core/auth/AuthContext';
import { PermissionProvider } from '@/core/permissions/PermissionContext';
import { AlertProvider } from '@/shared/alert/AlertContext';
import { ConfirmProvider } from '@/shared/confirm/ConfirmContext';
import { router } from './router';

// root component — bọc toàn bộ provider theo thứ tự:
// QueryClientProvider -> AuthProvider -> PermissionProvider -> AlertProvider -> ConfirmProvider -> RouterProvider
export const App = () => {
    return (
        <QueryClientProvider client={queryClient}>
            <AuthProvider>
                <PermissionProvider>
                    <AlertProvider>
                        <ConfirmProvider>
                            <RouterProvider router={router} />
                        </ConfirmProvider>
                    </AlertProvider>
                </PermissionProvider>
            </AuthProvider>
        </QueryClientProvider>
    );
};
