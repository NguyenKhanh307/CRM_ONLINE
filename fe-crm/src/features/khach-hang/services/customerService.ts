import axiosInstance from '@/core/axios/axiosInstance';
import type { ApiResponse, PageResult, PageParams } from '@/shared/types/api';
import type { CreateCustomerPayload, CustomerResult, UpdateCustomerPayload } from '../types/customerTypes';
import type { ImportOptions, ImportBulkResult } from '@/shared/components/import/importTypes';

export const customerService = {
    getList: (params?: PageParams) =>
        axiosInstance.get<ApiResponse<PageResult<CustomerResult>>>('/api/customers', { params }),
    create: (payload: CreateCustomerPayload) =>
        axiosInstance.post<ApiResponse<CustomerResult>>('/api/customers', payload),
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
    handoverBulk: (payload: { ids: number[]; toUserId: number; reason?: string }) =>
        axiosInstance.post('/api/customers/handover-bulk', payload),
    /** Kích hoạt khách hàng (→ active). */
    activate: (id: number) => axiosInstance.post<ApiResponse<CustomerResult>>(`/api/customers/${id}/activate`),
    /** Ngừng hoạt động khách hàng (→ inactive). */
    deactivate: (id: number) => axiosInstance.post<ApiResponse<CustomerResult>>(`/api/customers/${id}/deactivate`),
};
