import axiosInstance from '@/core/axios/axiosInstance';
import type { ApiResponse, PageResult, PageParams } from '@/shared/types/api';
import type { OrderResult } from '../types/orderTypes';

export const orderService = {
    getList: (params?: PageParams) =>
        axiosInstance.get<ApiResponse<PageResult<OrderResult>>>('/api/orders', { params }),
    getById: (id: number) =>
        axiosInstance.get<ApiResponse<OrderResult>>(`/api/orders/${id}`),
    remove: (id: number) =>
        axiosInstance.delete(`/api/orders/${id}`),
};
