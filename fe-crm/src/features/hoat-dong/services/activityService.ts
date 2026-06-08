import axiosInstance from '@/core/axios/axiosInstance';
import type { ApiResponse, PageResult, PageParams } from '@/shared/types/api';
import type { ActivityResult } from '../types/activityTypes';

export const activityService = {
    getList: (params?: PageParams) =>
        axiosInstance.get<ApiResponse<PageResult<ActivityResult>>>('/api/activities', { params }),
    getById: (id: number) =>
        axiosInstance.get<ApiResponse<ActivityResult>>(`/api/activities/${id}`),
    remove: (id: number) =>
        axiosInstance.delete(`/api/activities/${id}`),
};
