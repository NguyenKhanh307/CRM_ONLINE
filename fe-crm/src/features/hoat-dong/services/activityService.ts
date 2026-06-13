import axiosInstance from '@/core/axios/axiosInstance';
import type { ApiResponse, PageResult, PageParams } from '@/shared/types/api';
import type { ActivityResult, CreateActivityPayload, UpdateActivityPayload } from '../types/activityTypes';
import type { ImportOptions, ImportBulkResult } from '@/shared/components/import/importTypes';

export const activityService = {
    getList: (params?: PageParams) =>
        axiosInstance.get<ApiResponse<PageResult<ActivityResult>>>('/api/activities', { params }),
    create: (payload: CreateActivityPayload) =>
        axiosInstance.post<ApiResponse<ActivityResult>>('/api/activities', payload),
    getById: (id: number) =>
        axiosInstance.get<ApiResponse<ActivityResult>>(`/api/activities/${id}`),
    update: (id: number, payload: UpdateActivityPayload) =>
        axiosInstance.put<ApiResponse<ActivityResult>>(`/api/activities/${id}`, payload),
    remove: (id: number) =>
        axiosInstance.delete(`/api/activities/${id}`),
    importBulk: (rows: Record<string, unknown>[], options: ImportOptions) =>
        axiosInstance.post<ApiResponse<ImportBulkResult>>('/api/activities/import-bulk', {
            importType: options.importType,
            ownerMode: options.ownerMode,
            specificOwnerId: options.specificOwnerId ?? null,
            ownerFileColumn: options.ownerFileColumn ?? null,
            rows,
        }),
};
