import axiosInstance from '@/core/axios/axiosInstance';
import type { ApiResponse, PageResult, PageParams } from '@/shared/types/api';
import type { OrderResult, UpdateOrderPayload } from '../types/orderTypes';
import type { ImportOptions, ImportBulkResult } from '@/shared/components/import/importTypes';

export const orderService = {
    getList: (params?: PageParams) =>
        axiosInstance.get<ApiResponse<PageResult<OrderResult>>>('/api/orders', { params }),
    getById: (id: number) =>
        axiosInstance.get<ApiResponse<OrderResult>>(`/api/orders/${id}`),
    update: (id: number, payload: UpdateOrderPayload) =>
        axiosInstance.put<ApiResponse<OrderResult>>(`/api/orders/${id}`, payload),
    remove: (id: number) =>
        axiosInstance.delete(`/api/orders/${id}`),
    importBulk: (rows: Record<string, unknown>[], options: ImportOptions) =>
        axiosInstance.post<ApiResponse<ImportBulkResult>>('/api/orders/import-bulk', {
            importType: options.importType,
            ownerMode: options.ownerMode,
            specificOwnerId: options.specificOwnerId ?? null,
            ownerFileColumn: options.ownerFileColumn ?? null,
            rows,
        }),
    handoverBulk: (payload: { ids: number[]; toUserId: number; reason?: string }) =>
        axiosInstance.post('/api/orders/handover-bulk', payload),
};
