import axiosInstance from '@/core/axios/axiosInstance';
import type { ApiResponse, PageResult } from '@/shared/types/api';
import type { AuditLogEntry } from '../types/auditLogTypes';

export const auditLogService = {
    getList: (params: { source?: string | null; q?: string; page: number; size: number }) =>
        axiosInstance.get<ApiResponse<PageResult<AuditLogEntry>>>('/api/audit-log', {
            params: { source: params.source ?? undefined, q: params.q || undefined, page: params.page, size: params.size },
        }),
};
