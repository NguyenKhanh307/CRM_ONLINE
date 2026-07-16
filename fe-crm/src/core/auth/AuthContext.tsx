import { createContext, useState, useCallback, type ReactNode } from 'react';
import {
    getToken,
    setToken,
    getStoredUser,
    setStoredUser,
    clearSession,
    isTokenExpired,
} from './authStorage';

export interface AuthUser {
    id: number;
    email: string;
    fullName: string;
    avatarUrl?: string | null;
    roles: string[];
    permissions: string[];
}

interface AuthContextValue {
    user: AuthUser | null;
    token: string | null;
    login: (token: string, user: AuthUser) => void;
    updateUser: (patch: Partial<AuthUser>) => void;
    logout: () => void;
    isAuthenticated: boolean;
}

export const AuthContext = createContext<AuthContextValue | null>(null);

interface AuthProviderProps {
    children: ReactNode;
}

/**
 * Provider quản lý trạng thái xác thực toàn app.
 * Khởi tạo state từ localStorage để giữ session qua reload.
 */
export const AuthProvider = ({ children }: AuthProviderProps) => {
    const [token, setTokenState] = useState<string | null>(() => {
        const stored = getToken();
        if (stored && isTokenExpired(stored)) {
            clearSession();
            return null;
        }
        return stored;
    });
    const [user, setUser] = useState<AuthUser | null>(() => {
        const stored = getToken();
        if (!stored || isTokenExpired(stored)) return null;
        return getStoredUser<AuthUser>();
    });

    const login = useCallback((newToken: string, newUser: AuthUser) => {
        setToken(newToken);
        setStoredUser(newUser);
        setTokenState(newToken);
        setUser(newUser);
    }, []);

    /** Cập nhật một phần thông tin user đang lưu (vd sau khi sửa hồ sơ) — đồng bộ cả state lẫn localStorage. */
    const updateUser = useCallback((patch: Partial<AuthUser>) => {
        setUser((prev) => {
            if (!prev) return prev;
            const next = { ...prev, ...patch };
            setStoredUser(next);
            return next;
        });
    }, []);

    const logout = useCallback(() => {
        clearSession();
        setTokenState(null);
        setUser(null);
    }, []);

    return (
        <AuthContext.Provider
            value={{ user, token, login, updateUser, logout, isAuthenticated: !!token }}
        >
            {children}
        </AuthContext.Provider>
    );
};
