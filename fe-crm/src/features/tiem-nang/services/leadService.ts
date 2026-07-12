import axiosInstance from '@/core/axios/axiosInstance';
import type { ApiResponse, PageResult, PageParams } from '@/shared/types/api';
import type { CreateLeadPayload, LeadResult, UpdateLeadPayload } from '../types/leadTypes';
import type { ImportOptions, ImportBulkResult } from '@/shared/components/import/importTypes';

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
    /**
     * Chuyển đổi tiềm năng (qualified → converted).
     * `customerId` = dùng khách hàng đã có thay vì tạo mới (chống trùng khi phát hiện KH trùng MST/email/SĐT).
     */
    convert: (id: number, customerId?: number | null) =>
        axiosInstance.post<ApiResponse<LeadResult>>(`/api/leads/${id}/convert`, { customerId: customerId ?? null }),
    /** Đánh dấu tiềm năng đủ điều kiện thủ công (new/contacting → qualified), không cần đủ 50 điểm. */
    qualify: (id: number) => axiosInstance.post<ApiResponse<LeadResult>>(`/api/leads/${id}/qualify`),
    /** Đánh mất tiềm năng (→ lost). */
    lose: (id: number, reason?: string) =>
        axiosInstance.post<ApiResponse<LeadResult>>(`/api/leads/${id}/lose`, { reason }),
};
