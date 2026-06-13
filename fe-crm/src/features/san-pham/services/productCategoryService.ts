import axiosInstance from '@/core/axios/axiosInstance';
import type { ApiResponse, PageResult } from '@/shared/types/api';

export interface ProductCategoryResult {
    id: number;
    name: string;
}

export const productCategoryService = {
    getList: () =>
        axiosInstance.get<ApiResponse<PageResult<ProductCategoryResult>>>('/api/product-categories', {
            params: { size: 200 },
        }),
};
