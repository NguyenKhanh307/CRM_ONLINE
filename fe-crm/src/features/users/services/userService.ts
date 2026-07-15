import axiosInstance from '@/core/axios/axiosInstance';
import type { ApiResponse, PageResult } from '@/shared/types/api';
import type {
    ChangePasswordPayload,
    HandoverAllPayload,
    ProfileResult,
    RegisterEmployeePayload,
    UpdateProfilePayload,
    UserResult,
} from '../types/userTypes';

export const userService = {
    registerEmployee: (body: RegisterEmployeePayload) =>
        axiosInstance.post<ApiResponse<UserResult>>('/api/auth/register-employee', body),
    listActive: () =>
        axiosInstance.get<ApiResponse<PageResult<UserResult>>>('/api/users', { params: { status: 'active', size: 200 } }),
    handoverAll: (body: HandoverAllPayload) =>
        axiosInstance.post<ApiResponse<null>>('/api/handover/all', body),
    getMyProfile: () =>
        axiosInstance.get<ApiResponse<ProfileResult>>('/api/auth/me'),
    updateMyProfile: (body: UpdateProfilePayload) =>
        axiosInstance.put<ApiResponse<ProfileResult>>('/api/auth/me', body),
    changePassword: (body: ChangePasswordPayload) =>
        axiosInstance.post<ApiResponse<null>>('/api/auth/change-password', body),
};
