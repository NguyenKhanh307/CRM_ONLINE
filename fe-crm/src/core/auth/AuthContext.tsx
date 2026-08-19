import { createContext, useState, useCallback, type ReactNode } from 'react';
import {
    getToken,
    setToken,
    getStoredUser,
    setStoredUser,
    clearSession,
    isTokenExpired,
} from './authStorage';
// định nghĩa interface cho user xác thực
export interface AuthUser {
    id: number;
    email: string;
    fullName: string;
    avatarUrl?: string | null;
    roles: string[];
    permissions: string[];
}
// định nghĩa interface cho context xác thực
interface AuthContextValue {
    user: AuthUser | null;
    token: string | null;
    login: (token: string, user: AuthUser) => void;
    updateUser: (patch: Partial<AuthUser>) => void;
    logout: () => void;
    isAuthenticated: boolean;
}
// tạo context để cung cấp state xác thực cho toàn app
export const AuthContext = createContext<AuthContextValue | null>(null);

interface AuthProviderProps {
    children: ReactNode;
}

// provider quản lý trạng thái xác thực toàn app
export const AuthProvider = ({ children }: AuthProviderProps) => {
    // khởi tạo state từ localStorage để giữ session qua reload
    const [token, setTokenState] = useState<string | null>(() => {
        const stored = getToken();
        // nếu token hết hạn thì xóa session và trả về null
        if (stored && isTokenExpired(stored)) {
            clearSession();
            return null;
        }
        return stored;
    });
    // khởi tạo state user từ localStorage nếu token còn hiệu lực
    const [user, setUser] = useState<AuthUser | null>(() => {
        const stored = getToken();
        // nếu không có token hoặc token hết hạn thì trả về null
        if (!stored || isTokenExpired(stored)) return null;
        return getStoredUser<AuthUser>();
    });
    // hàm login — đồng bộ cả state lẫn localStorage
    const login = useCallback((newToken: string, newUser: AuthUser) => {
        setToken(newToken);
        setStoredUser(newUser);
        setTokenState(newToken);
        setUser(newUser);
    }, []);

    // cập nhật một phần thông tin user đang lưu (vd sau khi sửa hồ sơ) — đồng bộ cả state lẫn localStorage
    const updateUser = useCallback((patch: Partial<AuthUser>) => {
        setUser((prev) => {
            // nếu không có user trước đó thì không làm gì cả
            if (!prev) return prev;
            const next = { ...prev, ...patch };
            setStoredUser(next);
            return next;
        });
    }, []);
    // hàm logout — xóa session và reset state
    const logout = useCallback(() => {
        clearSession();
        setTokenState(null);
        setUser(null);
    }, []);

    return (
        // cung cấp state và hàm quản lý xác thực cho toàn app thông qua context
        <AuthContext.Provider
            // giá trị context bao gồm user, token, các hàm login, updateUser, logout và trạng thái isAuthenticated
            value={{ user, token, login, updateUser, logout, isAuthenticated: !!token }}
        >
            {children}
        </AuthContext.Provider>
    );
};
