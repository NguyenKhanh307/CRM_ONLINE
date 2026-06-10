const TOKEN_KEY = 'crm_access_token';
const USER_KEY = 'crm_user';

/** Lấy access token từ localStorage. */
export const getToken = (): string | null => localStorage.getItem(TOKEN_KEY);

/** Lưu access token vào localStorage. */
export const setToken = (token: string): void => localStorage.setItem(TOKEN_KEY, token);

/** Xóa access token khỏi localStorage. */
export const removeToken = (): void => localStorage.removeItem(TOKEN_KEY);

/** Lấy thông tin user đã lưu. */
export const getStoredUser = <T>(): T | null => {
    const raw = localStorage.getItem(USER_KEY);
    return raw ? (JSON.parse(raw) as T) : null;
};

/** Lưu thông tin user vào localStorage. */
export const setStoredUser = <T>(user: T): void =>
    localStorage.setItem(USER_KEY, JSON.stringify(user));

/** Xóa thông tin user khỏi localStorage. */
export const removeStoredUser = (): void => localStorage.removeItem(USER_KEY);

/** Xóa toàn bộ session (token + user). */
export const clearSession = (): void => {
    removeToken();
    removeStoredUser();
};

/** Kiểm tra JWT token đã hết hạn chưa (decode payload, không cần gọi API). */
export const isTokenExpired = (token: string): boolean => {
    try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        return payload.exp * 1000 < Date.now();
    } catch {
        return true;
    }
};
