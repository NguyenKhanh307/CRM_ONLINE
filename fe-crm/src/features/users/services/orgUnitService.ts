import axiosInstance from '@/core/axios/axiosInstance';
import type { ApiResponse, PageResult } from '@/shared/types/api';

export interface OrgUnitResult {
    id: number;
    code: string;
    name: string;
}

export const orgUnitService = {
    getList: () =>
        axiosInstance.get<ApiResponse<PageResult<OrgUnitResult>>>('/api/org-units', {
            params: { size: 200 },
        }),
};
