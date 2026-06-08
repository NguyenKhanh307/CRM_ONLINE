import axiosInstance from '@/core/axios/axiosInstance';
import type { ApiResponse, PageResult, PageParams } from '@/shared/types/api';
import type { CustomerResult } from '../types/customerTypes';

export const customerService = {
    getList: (params?: PageParams) =>
        axiosInstance.get<ApiResponse<PageResult<CustomerResult>>>('/api/customers', { params }),
    getById: (id: number) =>
        axiosInstance.get<ApiResponse<CustomerResult>>(`/api/customers/${id}`),
    remove: (id: number) =>
        axiosInstance.delete(`/api/customers/${id}`),
};
