import { useContext } from 'react';
import { AuthContext } from './AuthContext';

// hook truy cập AuthContext — phải dùng bên trong AuthProvider, sẽ throw nếu không có provider
export const useAuth = () => {
    const ctx = useContext(AuthContext);
    if (!ctx) throw new Error('useAuth must be used within AuthProvider');
    return ctx;
};
