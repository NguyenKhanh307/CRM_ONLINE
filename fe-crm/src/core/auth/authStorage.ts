const TOKEN_KEY = 'crm_access_token';
const USER_KEY = 'crm_user';

// lấy access token từ localStorage
export const getToken = (): string | null => localStorage.getItem(TOKEN_KEY);

// lưu access token vào localStorage
export const setToken = (token: string): void => localStorage.setItem(TOKEN_KEY, token);

// xóa access token khỏi localStorage
export const removeToken = (): void => localStorage.removeItem(TOKEN_KEY);

// lấy thông tin user đã lưu
export const getStoredUser = <T>(): T | null => {
    const raw = localStorage.getItem(USER_KEY);
    return raw ? (JSON.parse(raw) as T) : null;
};

// lưu thông tin user vào localStorage
export const setStoredUser = <T>(user: T): void =>
    localStorage.setItem(USER_KEY, JSON.stringify(user));

// xóa thông tin user khỏi localStorage
export const removeStoredUser = (): void => localStorage.removeItem(USER_KEY);

// xóa toàn bộ session (token + user)
export const clearSession = (): void => {
    removeToken();
    removeStoredUser();
};

// kiểm tra JWT token đã hết hạn chưa (decode payload, không cần gọi API)
export const isTokenExpired = (token: string): boolean => {
    try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        return payload.exp * 1000 < Date.now();
    } catch {
        return true;
    }
};
