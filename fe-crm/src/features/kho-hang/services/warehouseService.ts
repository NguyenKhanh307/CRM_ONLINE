import axiosInstance from '@/core/axios/axiosInstance';
import type { ApiResponse, PageResult, PageParams } from '@/shared/types/api';
import type { WarehouseResult } from '../types/warehouseTypes';

export const warehouseService = {
    getList: (params?: PageParams) =>
        axiosInstance.get<ApiResponse<PageResult<WarehouseResult>>>('/api/warehouses', { params }),
    getById: (id: number) =>
        axiosInstance.get<ApiResponse<WarehouseResult>>(`/api/warehouses/${id}`),
    remove: (id: number) =>
        axiosInstance.delete(`/api/warehouses/${id}`),
};
