import axiosInstance from '@/core/axios/axiosInstance';
import type { ApiResponse, PageResult, PageParams } from '@/shared/types/api';
import type { ImportOptions, ImportBulkResult } from '@/shared/components/import/importTypes';
import type {
    CreateTicketPayload, UpdateTicketPayload, TicketResult,
    TicketReturnItemPayload, TicketReturnItemResult, TicketComment,
    ResolutionType,
} from '../types/ticketTypes';

export const ticketService = {
    getList: (params?: PageParams) =>
        axiosInstance.get<ApiResponse<PageResult<TicketResult>>>('/api/tickets', { params }),
    getById: (id: number) =>
        axiosInstance.get<ApiResponse<TicketResult>>(`/api/tickets/${id}`),
    create: (payload: CreateTicketPayload) =>
        axiosInstance.post<ApiResponse<TicketResult>>('/api/tickets', payload),
    update: (id: number, payload: UpdateTicketPayload) =>
        axiosInstance.put<ApiResponse<TicketResult>>(`/api/tickets/${id}`, payload),
    remove: (id: number) =>
        axiosInstance.delete(`/api/tickets/${id}`),
    handoverBulk: (payload: { ids: number[]; toUserId: number; reason?: string }) =>
        axiosInstance.post('/api/tickets/handover-bulk', payload),
    importBulk: (rows: Record<string, unknown>[], options: ImportOptions) =>
        axiosInstance.post<ApiResponse<ImportBulkResult>>('/api/tickets/import-bulk', {
            importType: options.importType,
            ownerMode: options.ownerMode,
            specificOwnerId: options.specificOwnerId ?? null,
            ownerFileColumn: options.ownerFileColumn ?? null,
            rows,
        }),

    // ===== Dòng hàng trả/đổi =====
    getReturnItems: (ticketId: number) =>
        axiosInstance.get<ApiResponse<TicketReturnItemResult[]>>(`/api/tickets/${ticketId}/return-items`),
    createReturnItem: (ticketId: number, payload: TicketReturnItemPayload) =>
        axiosInstance.post<ApiResponse<TicketReturnItemResult>>(`/api/tickets/${ticketId}/return-items`, payload),
    updateReturnItem: (ticketId: number, itemId: number, payload: TicketReturnItemPayload) =>
        axiosInstance.put<ApiResponse<TicketReturnItemResult>>(`/api/tickets/${ticketId}/return-items/${itemId}`, payload),
    deleteReturnItem: (ticketId: number, itemId: number) =>
        axiosInstance.delete(`/api/tickets/${ticketId}/return-items/${itemId}`),

    // ===== Ghi chú / lịch sử =====
    getComments: (ticketId: number) =>
        axiosInstance.get<ApiResponse<TicketComment[]>>(`/api/tickets/${ticketId}/comments`),
    createComment: (ticketId: number, payload: { content: string; isInternal?: boolean }) =>
        axiosInstance.post<ApiResponse<TicketComment>>(`/api/tickets/${ticketId}/comments`, payload),

    // ===== Hành động luồng trạng thái =====
    assign: (id: number, toUserId: number) =>
        axiosInstance.post<ApiResponse<TicketResult>>(`/api/tickets/${id}/assign`, { toUserId }),
    start: (id: number) =>
        axiosInstance.post<ApiResponse<TicketResult>>(`/api/tickets/${id}/start`),
    resolve: (id: number, resolutionType?: ResolutionType, note?: string) =>
        axiosInstance.post<ApiResponse<TicketResult>>(`/api/tickets/${id}/resolve`, { resolutionType, note }),
    approve: (id: number, note?: string) =>
        axiosInstance.post<ApiResponse<TicketResult>>(`/api/tickets/${id}/approve`, { note }),
    reject: (id: number, reason?: string) =>
        axiosInstance.post<ApiResponse<TicketResult>>(`/api/tickets/${id}/reject`, { reason }),
    receive: (id: number) =>
        axiosInstance.post<ApiResponse<TicketResult>>(`/api/tickets/${id}/receive`),
    inspect: (id: number) =>
        axiosInstance.post<ApiResponse<TicketResult>>(`/api/tickets/${id}/inspect`),
    complete: (id: number, resolutionType?: ResolutionType, note?: string) =>
        axiosInstance.post<ApiResponse<TicketResult>>(`/api/tickets/${id}/complete`, { resolutionType, note }),
    close: (id: number) =>
        axiosInstance.post<ApiResponse<TicketResult>>(`/api/tickets/${id}/close`),
    reopen: (id: number) =>
        axiosInstance.post<ApiResponse<TicketResult>>(`/api/tickets/${id}/reopen`),
    csat: (id: number, score: number, comment?: string) =>
        axiosInstance.post<ApiResponse<TicketResult>>(`/api/tickets/${id}/csat`, { score, comment }),
};
