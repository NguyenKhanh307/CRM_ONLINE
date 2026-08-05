import axiosInstance from '@/core/axios/axiosInstance';
import type { ApiResponse, PageResult, PageParams } from '@/shared/types/api';
import type { CreateLeadItemPayload, CreateLeadPayload, LeadItemResult, LeadResult, UpdateLeadPayload } from '../types/leadTypes';
import type { ImportOptions, ImportBulkResult } from '@/shared/components/import/importTypes';
import type { LeadRelatedResult } from '@/shared/types/related';

export const leadService = {
    getList: (params?: PageParams) =>
        axiosInstance.get<ApiResponse<PageResult<LeadResult>>>('/api/leads', { params }),
    create: (payload: CreateLeadPayload) =>
        axiosInstance.post<ApiResponse<LeadResult>>('/api/leads', payload),
    getById: (id: number) =>
        axiosInstance.get<ApiResponse<LeadResult>>(`/api/leads/${id}`),
    update: (id: number, payload: UpdateLeadPayload) =>
        axiosInstance.put<ApiResponse<LeadResult>>(`/api/leads/${id}`, payload),
    remove: (id: number) =>
        axiosInstance.delete(`/api/leads/${id}`),
    importBulk: (rows: Record<string, unknown>[], options: ImportOptions) =>
        axiosInstance.post<ApiResponse<ImportBulkResult>>('/api/leads/import-bulk', {
            importType: options.importType,
            ownerMode: options.ownerMode,
            specificOwnerId: options.specificOwnerId ?? null,
            ownerFileColumn: options.ownerFileColumn ?? null,
            rows,
        }),
    handoverBulk: (payload: { ids: number[]; toUserId: number; reason?: string }) =>
        axiosInstance.post('/api/leads/handover-bulk', payload),
    // bản ghi liên quan cho trang chi tiết (hoạt động)
    getRelated: (id: number) =>
        axiosInstance.get<ApiResponse<LeadRelatedResult>>(`/api/leads/${id}/related`),
    // nhân viên tự nhận chăm sóc tiềm năng chưa có người phụ trách (pool chung)
    claim: (id: number) => axiosInstance.post<ApiResponse<LeadResult>>(`/api/leads/${id}/claim`),
    // sản phẩm quan tâm của tiềm năng (lead_items)
    getItems: (leadId: number) =>
        axiosInstance.get<ApiResponse<LeadItemResult[]>>(`/api/leads/${leadId}/items`),
    createItem: (leadId: number, payload: CreateLeadItemPayload) =>
        axiosInstance.post<ApiResponse<LeadItemResult>>(`/api/leads/${leadId}/items`, payload),
};
