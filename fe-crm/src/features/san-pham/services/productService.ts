import axiosInstance from '@/core/axios/axiosInstance';
import type { ApiResponse, PageResult, PageParams } from '@/shared/types/api';
import type { ProductResult, UpdateProductPayload } from '../types/productTypes';
import type { ImportOptions, ImportBulkResult } from '@/shared/components/import/importTypes';

export const productService = {
    getList: (params?: PageParams) =>
        axiosInstance.get<ApiResponse<PageResult<ProductResult>>>('/api/products', { params }),
    getById: (id: number) =>
        axiosInstance.get<ApiResponse<ProductResult>>(`/api/products/${id}`),
    update: (id: number, payload: UpdateProductPayload) =>
        axiosInstance.put<ApiResponse<ProductResult>>(`/api/products/${id}`, payload),
    remove: (id: number) =>
        axiosInstance.delete(`/api/products/${id}`),
    importBulk: (rows: Record<string, unknown>[], options: ImportOptions) =>
        axiosInstance.post<ApiResponse<ImportBulkResult>>('/api/products/import-bulk', {
            importType: options.importType,
            ownerMode: options.ownerMode,
            specificOwnerId: options.specificOwnerId ?? null,
            ownerFileColumn: options.ownerFileColumn ?? null,
            rows,
        }),
};
