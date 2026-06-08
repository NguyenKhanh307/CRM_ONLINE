import axiosInstance from '@/core/axios/axiosInstance';
import type { ApiResponse, PageResult, PageParams } from '@/shared/types/api';
import type { ProductResult } from '../types/productTypes';

export const productService = {
    getList: (params?: PageParams) =>
        axiosInstance.get<ApiResponse<PageResult<ProductResult>>>('/api/products', { params }),
    getById: (id: number) =>
        axiosInstance.get<ApiResponse<ProductResult>>(`/api/products/${id}`),
    remove: (id: number) =>
        axiosInstance.delete(`/api/products/${id}`),
};
