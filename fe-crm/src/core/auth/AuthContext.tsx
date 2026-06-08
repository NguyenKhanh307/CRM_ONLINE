import { createContext, useState, useCallback, type ReactNode } from 'react';
import {
    getToken,
    setToken,
    getStoredUser,
    setStoredUser,
    clearSession,
} from './authStorage';

export interface AuthUser {
    id: number;
    username: string;
    email: string;
    roles: string[];
}

interface AuthContextValue {
    user: AuthUser | null;
    token: string | null;
    login: (token: string, user: AuthUser) => void;
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
    const [token, setTokenState] = useState<string | null>(() => getToken());
    const [user, setUser] = useState<AuthUser | null>(() =>
        getStoredUser<AuthUser>()
    );

    const login = useCallback((newToken: string, newUser: AuthUser) => {
        setToken(newToken);
        setStoredUser(newUser);
        setTokenState(newToken);
        setUser(newUser);
    }, []);

    const logout = useCallback(() => {
        clearSession();
        setTokenState(null);
        setUser(null);
    }, []);

    return (
        <AuthContext.Provider
            value={{ user, token, login, logout, isAuthenticated: !!token }}
        >
            {children}
        </AuthContext.Provider>
    );
};
