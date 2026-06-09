import axiosInstance from '@/core/axios/axiosInstance';
import type { ApiResponse } from '@/shared/types/api';
import type { RegisterEmployeePayload, UserResult } from '../types/userTypes';

export const userService = {
    registerEmployee: (body: RegisterEmployeePayload) =>
        axiosInstance.post<ApiResponse<UserResult>>('/api/auth/register-employee', body),
};
