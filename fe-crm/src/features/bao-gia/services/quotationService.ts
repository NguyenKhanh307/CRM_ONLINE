import axiosInstance from '@/core/axios/axiosInstance';
import type { ApiResponse, PageResult, PageParams } from '@/shared/types/api';
import type { QuotationResult, UpdateQuotationPayload } from '../types/quotationTypes';
import type { ImportOptions, ImportBulkResult } from '@/shared/components/import/importTypes';

export const quotationService = {
    getList: (params?: PageParams) =>
        axiosInstance.get<ApiResponse<PageResult<QuotationResult>>>('/api/quotations', { params }),
    getById: (id: number) =>
        axiosInstance.get<ApiResponse<QuotationResult>>(`/api/quotations/${id}`),
    update: (id: number, payload: UpdateQuotationPayload) =>
        axiosInstance.put<ApiResponse<QuotationResult>>(`/api/quotations/${id}`, payload),
    remove: (id: number) =>
        axiosInstance.delete(`/api/quotations/${id}`),
    importBulk: (rows: Record<string, unknown>[], options: ImportOptions) =>
        axiosInstance.post<ApiResponse<ImportBulkResult>>('/api/quotations/import-bulk', {
            importType: options.importType,
            ownerMode: options.ownerMode,
            specificOwnerId: options.specificOwnerId ?? null,
            ownerFileColumn: options.ownerFileColumn ?? null,
            rows,
        }),
    handoverBulk: (payload: { ids: number[]; toUserId: number; reason?: string }) =>
        axiosInstance.post('/api/quotations/handover-bulk', payload),
};
