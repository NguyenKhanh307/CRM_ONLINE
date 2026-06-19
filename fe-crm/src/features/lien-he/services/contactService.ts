import axiosInstance from '@/core/axios/axiosInstance';
import type { ApiResponse, PageResult, PageParams } from '@/shared/types/api';
import type { ContactPhonePayload, ContactPhoneResult, ContactResult, CreateContactPayload, UpdateContactPayload } from '../types/contactTypes';
import type { ImportOptions, ImportBulkResult } from '@/shared/components/import/importTypes';

export const contactService = {
    getList: (params?: PageParams) =>
        axiosInstance.get<ApiResponse<PageResult<ContactResult>>>('/api/contacts', { params }),
    create: (payload: CreateContactPayload) =>
        axiosInstance.post<ApiResponse<ContactResult>>('/api/contacts', payload),
    getPhones: (contactId: number) =>
        axiosInstance.get<ApiResponse<ContactPhoneResult[]>>(`/api/contacts/${contactId}/phones`),
    createPhone: (contactId: number, payload: ContactPhonePayload) =>
        axiosInstance.post<ApiResponse<ContactPhoneResult>>(`/api/contacts/${contactId}/phones`, payload),
    updatePhone: (contactId: number, phoneId: number, payload: ContactPhonePayload) =>
        axiosInstance.put<ApiResponse<ContactPhoneResult>>(`/api/contacts/${contactId}/phones/${phoneId}`, payload),
    deletePhone: (contactId: number, phoneId: number) =>
        axiosInstance.delete(`/api/contacts/${contactId}/phones/${phoneId}`),
    getById: (id: number) =>
        axiosInstance.get<ApiResponse<ContactResult>>(`/api/contacts/${id}`),
    update: (id: number, payload: UpdateContactPayload) =>
        axiosInstance.put<ApiResponse<ContactResult>>(`/api/contacts/${id}`, payload),
    remove: (id: number) =>
        axiosInstance.delete(`/api/contacts/${id}`),
    importBulk: (rows: Record<string, unknown>[], options: ImportOptions) =>
        axiosInstance.post<ApiResponse<ImportBulkResult>>('/api/contacts/import-bulk', {
            importType: options.importType,
            ownerMode: options.ownerMode,
            specificOwnerId: options.specificOwnerId ?? null,
            ownerFileColumn: options.ownerFileColumn ?? null,
            rows,
        }),
};
