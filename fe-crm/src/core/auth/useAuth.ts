import { useContext } from 'react';
import { AuthContext } from './AuthContext';

/**
 * Hook truy cập AuthContext.
 * Phải dùng bên trong AuthProvider, sẽ throw nếu không có provider.
 */
export const useAuth = () => {
    const ctx = useContext(AuthContext);
    if (!ctx) throw new Error('useAuth must be used within AuthProvider');
    return ctx;
};
