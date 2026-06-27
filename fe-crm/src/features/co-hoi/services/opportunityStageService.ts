import axiosInstance from '@/core/axios/axiosInstance';
import type { ApiResponse, PageResult } from '@/shared/types/api';

export interface OpportunityStageResult {
    id: number;
    name: string;
    sortOrder: number;
    probability: number;
    isWon: boolean;
    isLost: boolean;
    createdAt?: string;
    updatedAt?: string;
}

/** Payload tạo/cập nhật giai đoạn pipeline. */
export interface OpportunityStagePayload {
    name: string;
    sortOrder: number;
    probability: number;
    isWon: boolean;
    isLost: boolean;
}

export const opportunityStageService = {
    /** Lấy danh sách giai đoạn (mặc định sắp theo sortOrder asc). */
    getList: () =>
        axiosInstance.get<ApiResponse<PageResult<OpportunityStageResult>>>('/api/opportunity-stages', {
            params: { size: 100 },
        }),
    /** Tạo giai đoạn mới. */
    create: (body: OpportunityStagePayload) =>
        axiosInstance.post<ApiResponse<OpportunityStageResult>>('/api/opportunity-stages', body),
    /** Cập nhật giai đoạn theo id. */
    update: (id: number, body: OpportunityStagePayload) =>
        axiosInstance.put<ApiResponse<OpportunityStageResult>>(`/api/opportunity-stages/${id}`, body),
    /** Xóa giai đoạn theo id. */
    remove: (id: number) => axiosInstance.delete(`/api/opportunity-stages/${id}`),
};
