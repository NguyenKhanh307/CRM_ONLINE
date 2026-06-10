import axiosInstance from '@/core/axios/axiosInstance';

export interface LoginPayload {
    email: string;
    password: string;
}

export interface LoginApiResponse {
    data: {
        token: string;
        id: number;
        email: string;
        fullName: string;
        roles: string[];
        permissions: string[];
    };
    message: string;
    status: number;
}

export const authService = {
    login: (body: LoginPayload) =>
        axiosInstance.post<LoginApiResponse>('/api/auth/login', body),
};
