import axiosInstance from '@/core/axios/axiosInstance';
import type { AuthUser } from '@/core/auth/AuthContext';

export interface LoginPayload {
    username: string;
    password: string;
}

export interface LoginResponse {
    token: string;
    user: AuthUser;
}

/**
 * Service xác thực — gọi API đăng nhập.
 */
export const authService = {
    /**
     * Gửi thông tin đăng nhập, nhận về token và thông tin user.
     * @param body - username và password
     */
    login: (body: LoginPayload) =>
        axiosInstance.post<LoginResponse>('/auth/login', body),
};
