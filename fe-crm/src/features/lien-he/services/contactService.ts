import axiosInstance from '@/core/axios/axiosInstance';
import type { ApiResponse, PageResult, PageParams } from '@/shared/types/api';
import type { ContactResult, CreateContactPayload, UpdateContactPayload } from '../types/contactTypes';
import type { ImportOptions, ImportBulkResult } from '@/shared/components/import/importTypes';
import type { ContactRelatedResult } from '@/shared/types/related';

export const contactService = {
    getList: (params?: PageParams) =>
        axiosInstance.get<ApiResponse<PageResult<ContactResult>>>('/api/contacts', { params }),
    create: (payload: CreateContactPayload) =>
        axiosInstance.post<ApiResponse<ContactResult>>('/api/contacts', payload),
    getById: (id: number) =>
        axiosInstance.get<ApiResponse<ContactResult>>(`/api/contacts/${id}`),
    update: (id: number, payload: UpdateContactPayload) =>
        axiosInstance.put<ApiResponse<ContactResult>>(`/api/contacts/${id}`, payload),
    remove: (id: number) =>
        axiosInstance.delete(`/api/contacts/${id}`),
    /** Bản ghi liên quan cho trang chi tiết (cơ hội, báo giá, đơn, hóa đơn, phiếu CS, hoạt động). */
    getRelated: (id: number) =>
        axiosInstance.get<ApiResponse<ContactRelatedResult>>(`/api/contacts/${id}/related`),
    importBulk: (rows: Record<string, unknown>[], options: ImportOptions) =>
        axiosInstance.post<ApiResponse<ImportBulkResult>>('/api/contacts/import-bulk', {
            importType: options.importType,
            ownerMode: options.ownerMode,
            specificOwnerId: options.specificOwnerId ?? null,
            ownerFileColumn: options.ownerFileColumn ?? null,
            rows,
        }),
};
