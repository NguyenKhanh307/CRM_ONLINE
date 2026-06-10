import axiosInstance from '@/core/axios/axiosInstance';
import type { ApiResponse, PageResult, PageParams } from '@/shared/types/api';
import type { CustomerResult, UpdateCustomerPayload } from '../types/customerTypes';
import type { ImportOptions, ImportBulkResult } from '@/shared/components/import/importTypes';

export const customerService = {
    getList: (params?: PageParams) =>
        axiosInstance.get<ApiResponse<PageResult<CustomerResult>>>('/api/customers', { params }),
    getById: (id: number) =>
        axiosInstance.get<ApiResponse<CustomerResult>>(`/api/customers/${id}`),
    update: (id: number, payload: UpdateCustomerPayload) =>
        axiosInstance.put<ApiResponse<CustomerResult>>(`/api/customers/${id}`, payload),
    remove: (id: number) =>
        axiosInstance.delete(`/api/customers/${id}`),
    importBulk: (rows: Record<string, unknown>[], options: ImportOptions) =>
        axiosInstance.post<ApiResponse<ImportBulkResult>>('/api/customers/import-bulk', {
            importType: options.importType,
            ownerMode: options.ownerMode,
            specificOwnerId: options.specificOwnerId ?? null,
            ownerFileColumn: options.ownerFileColumn ?? null,
            rows,
        }),
};
