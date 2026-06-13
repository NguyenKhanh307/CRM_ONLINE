import axiosInstance from '@/core/axios/axiosInstance';
import type { ApiResponse, PageResult, PageParams } from '@/shared/types/api';
import type { CreateOpportunityPayload, OpportunityResult, UpdateOpportunityPayload } from '../types/opportunityTypes';
import type { ImportOptions, ImportBulkResult } from '@/shared/components/import/importTypes';

export const opportunityService = {
    getList: (params?: PageParams) =>
        axiosInstance.get<ApiResponse<PageResult<OpportunityResult>>>('/api/opportunities', { params }),
    create: (payload: CreateOpportunityPayload) =>
        axiosInstance.post<ApiResponse<OpportunityResult>>('/api/opportunities', payload),
    getById: (id: number) =>
        axiosInstance.get<ApiResponse<OpportunityResult>>(`/api/opportunities/${id}`),
    update: (id: number, payload: UpdateOpportunityPayload) =>
        axiosInstance.put<ApiResponse<OpportunityResult>>(`/api/opportunities/${id}`, payload),
    remove: (id: number) =>
        axiosInstance.delete(`/api/opportunities/${id}`),
    importBulk: (rows: Record<string, unknown>[], options: ImportOptions) =>
        axiosInstance.post<ApiResponse<ImportBulkResult>>('/api/opportunities/import-bulk', {
            importType: options.importType,
            ownerMode: options.ownerMode,
            specificOwnerId: options.specificOwnerId ?? null,
            ownerFileColumn: options.ownerFileColumn ?? null,
            rows,
        }),
    handoverBulk: (payload: { ids: number[]; toUserId: number; reason?: string }) =>
        axiosInstance.post('/api/opportunities/handover-bulk', payload),
};
