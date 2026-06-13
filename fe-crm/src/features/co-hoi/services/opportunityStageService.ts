import axiosInstance from '@/core/axios/axiosInstance';
import type { ApiResponse, PageResult } from '@/shared/types/api';

export interface OpportunityStageResult {
    id: number;
    name: string;
}

export const opportunityStageService = {
    getList: () =>
        axiosInstance.get<ApiResponse<PageResult<OpportunityStageResult>>>('/api/opportunity-stages', {
            params: { size: 100 },
        }),
};
