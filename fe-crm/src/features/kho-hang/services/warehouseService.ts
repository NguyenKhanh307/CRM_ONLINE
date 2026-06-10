import axiosInstance from '@/core/axios/axiosInstance';
import type { ApiResponse, PageResult, PageParams } from '@/shared/types/api';
import type { WarehouseResult, UpdateWarehousePayload } from '../types/warehouseTypes';
import type { ImportOptions, ImportBulkResult } from '@/shared/components/import/importTypes';

export const warehouseService = {
    getList: (params?: PageParams) =>
        axiosInstance.get<ApiResponse<PageResult<WarehouseResult>>>('/api/warehouses', { params }),
    getById: (id: number) =>
        axiosInstance.get<ApiResponse<WarehouseResult>>(`/api/warehouses/${id}`),
    update: (id: number, payload: UpdateWarehousePayload) =>
        axiosInstance.put<ApiResponse<WarehouseResult>>(`/api/warehouses/${id}`, payload),
    remove: (id: number) =>
        axiosInstance.delete(`/api/warehouses/${id}`),
    importBulk: (rows: Record<string, unknown>[], options: ImportOptions) =>
        axiosInstance.post<ApiResponse<ImportBulkResult>>('/api/warehouses/import-bulk', {
            importType: options.importType,
            ownerMode: options.ownerMode,
            specificOwnerId: options.specificOwnerId ?? null,
            ownerFileColumn: options.ownerFileColumn ?? null,
            rows,
        }),
};
