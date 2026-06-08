import axios from 'axios';
import { getToken, clearSession } from '@/core/auth/authStorage';

const axiosInstance = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL as string,
    timeout: 15_000,
    headers: { 'Content-Type': 'application/json' },
});

/** Đính kèm Bearer token vào mỗi request nếu có. */
axiosInstance.interceptors.request.use((config) => {
    const token = getToken();
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

/**
 * Xử lý response lỗi toàn cục.
 * 401 → xóa session và chuyển về trang login.
 */
axiosInstance.interceptors.response.use(
    (response) => response,
    (error: unknown) => {
        if (axios.isAxiosError(error) && error.response?.status === 401) {
            clearSession();
            window.location.href = '/login';
        }
        return Promise.reject(error);
    }
);

export default axiosInstance;
