import axiosInstance from '@/core/axios/axiosInstance';
import type { ApiResponse, PageResult, PageParams } from '@/shared/types/api';
import type {
    CampaignResult, CreateCampaignPayload, UpdateCampaignPayload,
    CampaignMemberResult, CreateCampaignMemberPayload, CampaignStatsResult, SendCampaignEmailPayload,
} from '../types/campaignTypes';
import type { ImportOptions, ImportBulkResult } from '@/shared/components/import/importTypes';
import type { CampaignRelatedResult } from '@/shared/types/related';

export const campaignService = {
    getList: (params?: PageParams) =>
        axiosInstance.get<ApiResponse<PageResult<CampaignResult>>>('/api/campaigns', { params }),
    getById: (id: number) =>
        axiosInstance.get<ApiResponse<CampaignResult>>(`/api/campaigns/${id}`),
    create: (payload: CreateCampaignPayload) =>
        axiosInstance.post<ApiResponse<CampaignResult>>('/api/campaigns', payload),
    update: (id: number, payload: UpdateCampaignPayload) =>
        axiosInstance.put<ApiResponse<CampaignResult>>(`/api/campaigns/${id}`, payload),
    remove: (id: number) =>
        axiosInstance.delete(`/api/campaigns/${id}`),
    getStats: (id: number) =>
        axiosInstance.get<ApiResponse<CampaignStatsResult>>(`/api/campaigns/${id}/stats`),
    // ----- Thành viên -----
    getMembers: (id: number) =>
        axiosInstance.get<ApiResponse<CampaignMemberResult[]>>(`/api/campaigns/${id}/members`),
    createMember: (id: number, payload: CreateCampaignMemberPayload) =>
        axiosInstance.post<ApiResponse<CampaignMemberResult>>(`/api/campaigns/${id}/members`, payload),
    deleteMember: (id: number, memberId: number) =>
        axiosInstance.delete(`/api/campaigns/${id}/members/${memberId}`),
    // ----- Gửi email -----
    sendEmail: (id: number, payload: SendCampaignEmailPayload) =>
        axiosInstance.post<ApiResponse<number>>(`/api/campaigns/${id}/send-email`, payload),
    // ----- Workflow -----
    schedule: (id: number) => axiosInstance.post<ApiResponse<CampaignResult>>(`/api/campaigns/${id}/schedule`),
    start: (id: number) => axiosInstance.post<ApiResponse<CampaignResult>>(`/api/campaigns/${id}/start`),
    pause: (id: number) => axiosInstance.post<ApiResponse<CampaignResult>>(`/api/campaigns/${id}/pause`),
    complete: (id: number) => axiosInstance.post<ApiResponse<CampaignResult>>(`/api/campaigns/${id}/complete`),
    cancel: (id: number) => axiosInstance.post<ApiResponse<CampaignResult>>(`/api/campaigns/${id}/cancel`),
    // ----- Import / Handover -----
    importBulk: (rows: Record<string, unknown>[], options: ImportOptions) =>
        axiosInstance.post<ApiResponse<ImportBulkResult>>('/api/campaigns/import-bulk', {
            importType: options.importType,
            ownerMode: options.ownerMode,
            specificOwnerId: options.specificOwnerId ?? null,
            ownerFileColumn: options.ownerFileColumn ?? null,
            rows,
        }),
    handoverBulk: (payload: { ids: number[]; toUserId: number; reason?: string }) =>
        axiosInstance.post('/api/campaigns/handover-bulk', payload),
    // ----- Bản ghi quy về chiến dịch (trang chi tiết) -----
    getRelated: (id: number) =>
        axiosInstance.get<ApiResponse<CampaignRelatedResult>>(`/api/campaigns/${id}/related`),
};
