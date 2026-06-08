import axiosInstance from '@/core/axios/axiosInstance';
import type { ApiResponse, PageResult, PageParams } from '@/shared/types/api';
import type { LeadResult } from '../types/leadTypes';

export const leadService = {
    getList: (params?: PageParams) =>
        axiosInstance.get<ApiResponse<PageResult<LeadResult>>>('/api/leads', { params }),
    getById: (id: number) =>
        axiosInstance.get<ApiResponse<LeadResult>>(`/api/leads/${id}`),
    remove: (id: number) =>
        axiosInstance.delete(`/api/leads/${id}`),
};
