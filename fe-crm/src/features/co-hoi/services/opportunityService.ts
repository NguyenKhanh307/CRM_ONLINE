import axiosInstance from '@/core/axios/axiosInstance';
import type { ApiResponse, PageResult, PageParams } from '@/shared/types/api';
import type { CreateOpportunityPayload, OpportunityItemPayload, OpportunityItemResult, OpportunityResult, UpdateOpportunityPayload } from '../types/opportunityTypes';
import type { ImportOptions, ImportBulkResult } from '@/shared/components/import/importTypes';

export const opportunityService = {
    getList: (params?: PageParams) =>
        axiosInstance.get<ApiResponse<PageResult<OpportunityResult>>>('/api/opportunities', { params }),
    create: (payload: CreateOpportunityPayload) =>
        axiosInstance.post<ApiResponse<OpportunityResult>>('/api/opportunities', payload),
    getItems: (opportunityId: number) =>
        axiosInstance.get<ApiResponse<OpportunityItemResult[]>>(`/api/opportunities/${opportunityId}/items`),
    createItem: (opportunityId: number, payload: OpportunityItemPayload) =>
        axiosInstance.post<ApiResponse<OpportunityItemResult>>(`/api/opportunities/${opportunityId}/items`, payload),
    updateItem: (opportunityId: number, itemId: number, payload: OpportunityItemPayload) =>
        axiosInstance.put<ApiResponse<OpportunityItemResult>>(`/api/opportunities/${opportunityId}/items/${itemId}`, payload),
    deleteItem: (opportunityId: number, itemId: number) =>
        axiosInstance.delete(`/api/opportunities/${opportunityId}/items/${itemId}`),
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
