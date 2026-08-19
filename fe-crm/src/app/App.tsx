import { RouterProvider } from 'react-router-dom';
import { AuthProvider } from '@/core/auth/AuthContext';
import { PermissionProvider } from '@/core/permissions/PermissionContext';
import { AlertProvider } from '@/shared/alert/AlertContext';
import { ConfirmProvider } from '@/shared/confirm/ConfirmContext';
import { router } from './router';

// root component — bọc toàn bộ provider theo thứ tự:
// AuthProvider -> PermissionProvider -> AlertProvider -> ConfirmProvider -> RouterProvider
export const App = () => {
    return (
        <AuthProvider>
            <PermissionProvider>
                <AlertProvider>
                    <ConfirmProvider>
                        <RouterProvider router={router} />
                    </ConfirmProvider>
                </AlertProvider>
            </PermissionProvider>
        </AuthProvider>
    );
};
