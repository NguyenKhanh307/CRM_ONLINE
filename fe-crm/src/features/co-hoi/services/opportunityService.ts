import axiosInstance from '@/core/axios/axiosInstance';
import type { ApiResponse, PageResult, PageParams } from '@/shared/types/api';
import type { OpportunityResult } from '../types/opportunityTypes';

export const opportunityService = {
    getList: (params?: PageParams) =>
        axiosInstance.get<ApiResponse<PageResult<OpportunityResult>>>('/api/opportunities', { params }),
    getById: (id: number) =>
        axiosInstance.get<ApiResponse<OpportunityResult>>(`/api/opportunities/${id}`),
    remove: (id: number) =>
        axiosInstance.delete(`/api/opportunities/${id}`),
};
