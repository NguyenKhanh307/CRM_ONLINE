import axiosInstance from '@/core/axios/axiosInstance';
import type { ApiResponse, PageResult } from '@/shared/types/api';
import type { HandoverAllPayload, RegisterEmployeePayload, UserResult } from '../types/userTypes';

export const userService = {
    registerEmployee: (body: RegisterEmployeePayload) =>
        axiosInstance.post<ApiResponse<UserResult>>('/api/auth/register-employee', body),
    listActive: () =>
        axiosInstance.get<ApiResponse<PageResult<UserResult>>>('/api/users', { params: { status: 'active', size: 200 } }),
    handoverAll: (body: HandoverAllPayload) =>
        axiosInstance.post<ApiResponse<null>>('/api/handover/all', body),
};
