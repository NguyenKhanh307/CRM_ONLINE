import axiosInstance from '@/core/axios/axiosInstance';
import type { ApiResponse } from '@/shared/types/api';
import type { ActivateAccountPayload, UserResult } from '@/features/users/types/userTypes';

export const activationService = {
    activate: (body: ActivateAccountPayload) =>
        axiosInstance.post<ApiResponse<UserResult>>('/api/auth/activate', body),
};
